package com.chat.backend.repository;

import com.chat.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByPhone(String phone);
    
	Optional<User> findByEmail(String email); // ✅ Needed for Google OAuth check
    

    List<User> findByUsernameContainingIgnoreCaseOrPhoneContaining(
        String username,
        String phone
    );
 // Add this to find users for the "New Chat" search
    List<User> findByPhoneContaining(String phone);
}