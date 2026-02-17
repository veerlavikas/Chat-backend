package com.chat.backend.service;

import com.chat.backend.dto.SignupRequest;
import com.chat.backend.entity.User;
import com.chat.backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    // 1. We define the fields with clear names
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 2. The constructor matches these names exactly
    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(SignupRequest dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPhone(dto.getPhone());
        
        // 3. FIX: Changed 'encoder' to 'passwordEncoder' to match the field name above
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        
        // 4. FIX: Changed 'userRepo' to 'userRepository' to match the field name above
        return userRepository.save(user);
    }

    public User login(String phone) {
        // 5. FIX: Changed 'userRepo' to 'userRepository'
        return userRepository.findByPhone(phone);
    }
}