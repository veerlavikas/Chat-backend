package com.chat.backend.repository;

import com.chat.backend.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface StatusRepository extends JpaRepository<Status, Long> {
    
    // Find all active statuses from all users (last 24h)
    List<Status> findByExpiresAtAfter(LocalDateTime now);

    // ✅ Add this to fix the "undefined" error
    // Find a specific user's active statuses
    List<Status> findByUserIdAndExpiresAtAfter(Long userId, LocalDateTime now);
}