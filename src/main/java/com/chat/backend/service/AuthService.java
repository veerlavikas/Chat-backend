package com.chat.backend.service;

import com.chat.backend.dto.SignupRequest;
import com.chat.backend.entity.User;
import com.chat.backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // ✅ FIXED: Removed JavaMailSender to stop the "Bean Not Found" crash
    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(SignupRequest dto) {
        // Use existsByPhone for better performance if you have it in your repo
        if (userRepository.findByPhone(dto.getPhone()).isPresent()) {
            throw new RuntimeException("User with this phone already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail()); 
        user.setPhone(dto.getPhone()); 
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        
        // Set a default status so it's not null
        user.setStatus("Hey there! I am using ChatApp");
        
        return userRepository.save(user);
    }

    public Optional<User> login(String phone) {
        return userRepository.findByPhone(phone); 
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // ❌ REMOVED sendOtpEmail: This logic now lives in EmailService.java 
    // and is called by AuthController.sendOtp.
}