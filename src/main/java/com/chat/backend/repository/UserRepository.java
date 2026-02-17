package com.chat.backend.repository;

import com.chat.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByPhone(String phone);

    // ✅ REQUIRED FOR NEW CHAT SEARCH
    List<User> findByUsernameContainingIgnoreCaseOrPhoneContaining(
        String username,
        String phone
    );
}
