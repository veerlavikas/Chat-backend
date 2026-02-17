package com.chat.backend.service;

import com.chat.backend.dto.SignupRequest;
import com.chat.backend.entity.User;
import com.chat.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder;
    @Autowired

    public AuthService(UserRepository userRepo, BCryptPasswordEncoder encoder) {
		super();
		this.userRepo = userRepo;
		this.encoder = encoder;
	}

	public User register(SignupRequest dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setPassword(encoder.encode(dto.getPassword()));
        return userRepo.save(user);
    }

    public User login(String phone) {
        return userRepo.findByPhone(phone);
    }
}
