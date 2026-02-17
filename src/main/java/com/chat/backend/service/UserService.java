package com.chat.backend.service;

import com.chat.backend.entity.User;
import com.chat.backend.repository.UserRepository;
import com.chat.backend.security.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repo;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository repo, JwtUtil jwtUtil) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
    }

    public User getByPhone(String phone) {
        return repo.findByPhone(phone);
    }

    public User getUserFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String phone = jwtUtil.extractPhone(token);
        return repo.findByPhone(phone);
    }

    public User save(User user) {
        return repo.save(user);
    }
}
