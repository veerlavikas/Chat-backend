package com.chat.backend.controller;

import com.chat.backend.dto.LoginRequest;
import com.chat.backend.dto.SignupRequest;
import com.chat.backend.entity.OtpVerification;
import com.chat.backend.entity.User;
import com.chat.backend.repository.OtpRepository;
import com.chat.backend.repository.UserRepository;
import com.chat.backend.security.JwtUtil;
import com.chat.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;
    private final OtpRepository otpRepository; // Added

    public AuthController(AuthService authService, JwtUtil jwtUtil, BCryptPasswordEncoder encoder, OtpRepository otpRepository) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
        this.otpRepository = otpRepository;
    }

    // Step 1: Send OTP
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);
        
        OtpVerification otp = new OtpVerification(phone, code);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5)); // Set 
        otpRepository.save(otp);
        
        System.out.println("OTP for " + phone + " is: " + code);
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
    }

    // Step 2: Verify OTP and Register
    @PostMapping("/verify-signup")
    public ResponseEntity<?> verifyAndSignup(@RequestBody SignupRequest dto) {
        Optional<OtpVerification> otpData = otpRepository.findTopByPhoneOrderByExpiryTimeDesc(dto.getPhone());

        if (otpData.isPresent() && 
            otpData.get().getCode().equals(dto.getOtp()) && 
            otpData.get().getExpiryTime().isAfter(LocalDateTime.now())) {
            
            User user = authService.register(dto);
            otpRepository.deleteByPhone(dto.getPhone()); // Cleanup
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(400).body("Invalid or expired OTP");
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest dto) {
        User dbUser = authService.login(dto.getPhone());
        if (dbUser == null || !encoder.matches(dto.getPassword(), dbUser.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(dbUser.getPhone());
        return Map.of("token", token);
    }
}