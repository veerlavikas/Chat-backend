package com.chat.backend.service;

import com.chat.backend.entity.User;
import com.chat.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repo;

    @Autowired
    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public Optional<User> getByPhone(String phone) {
        return repo.findByPhone(phone);
    }

    @Transactional
    public User syncUser(String phone) {
        // ✅ Correct way to handle Optional: find it, or create a new one if empty
        return repo.findByPhone(phone).orElseGet(() -> {
            User newUser = new User();
            newUser.setPhone(phone);
            newUser.setUsername("User_" + phone.substring(phone.length() - 4)); // Default username
            return repo.save(newUser);
        });
    }

    public User save(User user) {
        return repo.save(user);
    }
}