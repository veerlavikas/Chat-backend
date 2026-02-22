package com.chat.backend.repository;

import com.chat.backend.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpVerification, Long> {
    
    // Changed Phone to Email
    Optional<OtpVerification> findTopByEmailOrderByExpiryTimeDesc(String email);
    
    // Changed Phone to Email
    void deleteByEmail(String email);
}