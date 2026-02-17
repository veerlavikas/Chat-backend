package com.chat.backend.controller;

import com.chat.backend.dto.LoginRequest;
import com.chat.backend.dto.SignupRequest;
import com.chat.backend.entity.User;
import com.chat.backend.security.JwtUtil;
import com.chat.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;

    // Manual constructor - Spring uses this for injection
    public AuthController(AuthService authService, JwtUtil jwtUtil, BCryptPasswordEncoder encoder) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
    }

    @PostMapping("/signup")
    public User signup(@RequestBody SignupRequest dto) {
        return authService.register(dto);
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