package com.chat.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "otp_verifications")
public class OtpVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String phone;
    private String code;
    private LocalDateTime expiryTime;

    public OtpVerification() {}
    public OtpVerification(String phone, String code) {
        this.phone = phone;
        this.code = code;
        this.expiryTime = LocalDateTime.now().plusMinutes(5); // Valid for 5 mins
    }
	public  Long getId() {
		return id;
	}
	public  void setId(Long id) {
		this.id = id;
	}
	public  String getPhone() {
		return phone;
	}
	public  void setPhone(String phone) {
		this.phone = phone;
	}
	public  String getCode() {
		return code;
	}
	public  void setCode(String code) {
		this.code = code;
	}
	public  LocalDateTime getExpiryTime() {
		return expiryTime;
	}
	public  void setExpiryTime(LocalDateTime expiryTime) {
		this.expiryTime = expiryTime;
	}
    
}