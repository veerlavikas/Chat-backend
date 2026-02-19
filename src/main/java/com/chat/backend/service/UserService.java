package com.chat.backend.service;

import com.chat.backend.entity.User;
import com.chat.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository repo;

    @Autowired
    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User getByPhone(String phone) {
        return repo.findByPhone(phone);
    }

    // ✅ Sync Firebase User with TiDB
    @Transactional
    public User syncUser(String phone) {
        User user = repo.findByPhone(phone);
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            // Leave username null so ProfileSetupScreen can handle it!
            user.setUsername(null); 
            return repo.save(user);
        }
        return user;
    }

    public User save(User user) {
        return repo.save(user);
    }
}