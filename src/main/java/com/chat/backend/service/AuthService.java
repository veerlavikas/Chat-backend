package com.chat.backend.service;

import com.chat.backend.dto.SignupRequest;
import com.chat.backend.entity.User;
import com.chat.backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender; // ✅ Injected Mail Sender

    // ✅ Updated Constructor
    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    public User register(SignupRequest dto) {
        if (userRepository.findByPhone(dto.getPhone()).isPresent()) {
            throw new RuntimeException("User with this phone already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail()); 
        user.setPhone(dto.getPhone()); 
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> login(String phone) {
        return userRepository.findByPhone(phone); 
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * ✅ NEW: Method to actually send the OTP Email
     */
    public void sendOtpEmail(String toEmail, String otpCode) {
        // 1. MUST-HAVE FOR TESTING: Print to console so you can bypass the email wait!
        System.out.println("\n========================================");
        System.out.println("🔔 DEBUG OTP FOR " + toEmail + " IS: " + otpCode);
        System.out.println("========================================\n");

        // 2. Try to send the actual email
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("veerlavikas9294@gmail.com"); // Matches your properties file
            message.setTo(toEmail);
            message.setSubject("Your Chat App Verification Code");
            message.setText("Welcome to the Chat App!\n\nYour 6-digit verification code is: " + otpCode + "\n\nThis code will expire in 10 minutes.");
            
            mailSender.send(message);
            System.out.println("✅ Email successfully sent to " + toEmail);
            
        } catch (Exception e) {
            // If Gmail blocks it, this catch block will tell us exactly why!
            System.err.println("❌ FAILED TO SEND EMAIL to " + toEmail);
            System.err.println("Reason: " + e.getMessage());
        }
    }
}