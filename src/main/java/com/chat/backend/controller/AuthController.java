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
import org.springframework.transaction.annotation.Transactional; // ✅ CRITICAL: Added Transactional import
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
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

    // ✅ iOS Client ID used by Expo Go
    private final String googleIosClientId = "935335713515-auit82o1a9opsld52ge1t7lft2v0809g.apps.googleusercontent.com";

    public AuthController(AuthService authService, JwtUtil jwtUtil, BCryptPasswordEncoder encoder, OtpRepository otpRepository, JavaMailSender mailSender) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
        this.otpRepository = otpRepository;
        this.mailSender = mailSender;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email"); 
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);
        
        OtpVerification otp = new OtpVerification(email, code);
        otpRepository.save(otp);

        // ✅ TERMINAL BYPASS
        System.out.println("\n========================================");
        System.out.println("🔔 DEBUG OTP FOR " + email + " IS: " + code);
        System.out.println("========================================\n");

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("veerlavikas9294@gmail.com"); 
            message.setTo(email);
            message.setSubject("Chat App Verification Code");
            message.setText("Your verification code is: " + code);
            mailSender.send(message);
            return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
        } catch (Exception e) {
            System.err.println("❌ GMAIL ERROR: " + e.getMessage());
            e.printStackTrace(); 
            return ResponseEntity.status(500).body(Map.of("message", "Failed to send email. Check server console."));
        }
    }

    // ✅ CRITICAL FIX: Added @Transactional and try-catch wrapper
    @Transactional 
    @PostMapping("/verify-signup")
    public ResponseEntity<?> verifyAndSignup(@RequestBody SignupRequest dto) {
        Optional<OtpVerification> otpData = otpRepository.findTopByEmailOrderByExpiryTimeDesc(dto.getEmail());
        if (otpData.isPresent() && 
            otpData.get().getCode().equals(dto.getOtp()) && 
            otpData.get().getExpiryTime().isAfter(LocalDateTime.now())) {
            
            try {
                // Try to save the user
                User user = authService.register(dto);
                // Try to delete the OTP (This now works because of @Transactional)
                otpRepository.deleteByEmail(dto.getEmail()); 
                
                String token = jwtUtil.generateToken(user.getPhone());
                return ResponseEntity.ok(Map.of("token", token, "user", user));
                
            } catch (RuntimeException e) {
                // If phone exists, fail gracefully with 400 Bad Request
                return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
            }
        }
        return ResponseEntity.status(400).body(Map.of("message", "Invalid or expired OTP"));
    }

    @PostMapping("/login") 
    public Map<String, String> login(@RequestBody LoginRequest dto) {
        User dbUser = authService.login(dto.getPhone())
                .orElseThrow(() -> new RuntimeException("User not found")); 

        if (!encoder.matches(dto.getPassword(), dbUser.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        String token = jwtUtil.generateToken(dbUser.getPhone()); 
        return Map.of("token", token);
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleAuth(@RequestBody Map<String, String> request) {
        String idTokenString = request.get("idToken");
        String phone = request.get("phone"); 

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Arrays.asList(googleClientId, googleIosClientId)) 
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");

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
                return ResponseEntity.status(401).body(Map.of("message", "Invalid Google ID token."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Google authentication failed: " + e.getMessage()));
        }
    }
}