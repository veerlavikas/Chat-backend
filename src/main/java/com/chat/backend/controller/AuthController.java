package com.chat.backend.controller;

import com.chat.backend.dto.LoginRequest;
import com.chat.backend.dto.SignupRequest;
import com.chat.backend.entity.User;
import com.chat.backend.security.JwtUtil;
import com.chat.backend.service.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;
    
    @Autowired
    
    public AuthController(AuthService authService, JwtUtil jwtUtil, BCryptPasswordEncoder encoder) {
		super();
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
