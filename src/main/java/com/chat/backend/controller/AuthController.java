package com.chat.backend.controller;

import com.chat.backend.dto.LoginRequest;
import com.chat.backend.dto.SignupRequest;
import com.chat.backend.entity.OtpVerification;
import com.chat.backend.entity.User;
import com.chat.backend.repository.OtpRepository;
import com.chat.backend.security.JwtUtil;
import com.chat.backend.service.AuthService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;
    private final OtpRepository otpRepository;
    private final JavaMailSender mailSender; 

    @Value("${google.client.id}")
    private String googleClientId;

    public AuthController(AuthService authService, JwtUtil jwtUtil, BCryptPasswordEncoder encoder, OtpRepository otpRepository, JavaMailSender mailSender) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
        this.otpRepository = otpRepository;
        this.mailSender = mailSender;
    }

    // ✅ SEND OTP (Unchanged)
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email"); 
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);
        OtpVerification otp = new OtpVerification(email, code);
        otpRepository.save(otp);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Chat App Verification Code");
            message.setText("Your verification code is: " + code);
            mailSender.send(message);
            return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to send email"));
        }
    }

    // ✅ VERIFY SIGNUP (Unchanged)
    @PostMapping("/verify-signup")
    public ResponseEntity<?> verifyAndSignup(@RequestBody SignupRequest dto) {
        Optional<OtpVerification> otpData = otpRepository.findTopByEmailOrderByExpiryTimeDesc(dto.getEmail());
        if (otpData.isPresent() && 
            otpData.get().getCode().equals(dto.getOtp()) && 
            otpData.get().getExpiryTime().isAfter(LocalDateTime.now())) {
            User user = authService.register(dto);
            otpRepository.deleteByEmail(dto.getEmail()); 
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(400).body("Invalid or expired OTP");
    }

    /**
     * ✅ FIXED LOGIN: Handled Optional<User>
     */
    @PostMapping("/login") 
    public Map<String, String> login(@RequestBody LoginRequest dto) {
        // authService.login returns Optional<User>
        User dbUser = authService.login(dto.getPhone())
                .orElseThrow(() -> new RuntimeException("User not found")); 

        if (!encoder.matches(dto.getPassword(), dbUser.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        String token = jwtUtil.generateToken(dbUser.getPhone()); 
        return Map.of("token", token);
    }

    /**
     * ✅ FIXED GOOGLE AUTH: Handled Optional types
     */
    @PostMapping("/google")
    public ResponseEntity<?> googleAuth(@RequestBody Map<String, String> request) {
        String idTokenString = request.get("idToken");
        String phone = request.get("phone"); 

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId)) 
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                // ✅ Change 1: Get Optional and check if present
                Optional<User> userOpt = authService.getUserByEmail(email); 
                User finalUser;

                if (userOpt.isEmpty()) {
                    if (phone == null || phone.isEmpty()) {
                        return ResponseEntity.status(202).body(Map.of(
                                "status", "REQUIRES_PHONE",
                                "email", email,
                                "name", name,
                                "message", "Please provide a phone number to complete signup."
                        ));
                    }
                    
                    SignupRequest newGoogleUser = new SignupRequest();
                    newGoogleUser.setEmail(email);
                    newGoogleUser.setUsername(name);
                    newGoogleUser.setPhone(phone);
                    newGoogleUser.setPassword(UUID.randomUUID().toString()); 
                    
                    finalUser = authService.register(newGoogleUser);
                } else {
                    finalUser = userOpt.get();
                }

                String jwt = jwtUtil.generateToken(finalUser.getPhone());
                return ResponseEntity.ok(Map.of("token", jwt, "phone", finalUser.getPhone()));

            } else {
                return ResponseEntity.status(401).body("Invalid Google ID token.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Google authentication failed: " + e.getMessage());
        }
    }
}