package com.chat.backend.repository;

import com.chat.backend.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpVerification, Long> {
    
    // To find the latest code sent to a specific phone number
    Optional<OtpVerification> findTopByPhoneOrderByExpiryTimeDesc(String phone);
    
    // To clean up expired codes (optional, but good for database health)
    void deleteByPhone(String phone);
}