package com.chat.backend.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "otp_verifications")
public class OtpVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Changed from phone to email
    private String email; 
    private String code;
    private LocalDateTime expiryTime;

    public OtpVerification() {}
    
    // Changed constructor parameter
    public OtpVerification(String email, String code) {
        this.email = email;
        this.code = code;
        this.expiryTime = LocalDateTime.now().plusMinutes(5); // Valid for 5 mins
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    // Changed Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public LocalDateTime getExpiryTime() { return expiryTime; }
    public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }
}