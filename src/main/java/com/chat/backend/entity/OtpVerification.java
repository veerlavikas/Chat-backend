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
	public final Long getId() {
		return id;
	}
	public final void setId(Long id) {
		this.id = id;
	}
	public final String getPhone() {
		return phone;
	}
	public final void setPhone(String phone) {
		this.phone = phone;
	}
	public final String getCode() {
		return code;
	}
	public final void setCode(String code) {
		this.code = code;
	}
	public final LocalDateTime getExpiryTime() {
		return expiryTime;
	}
	public final void setExpiryTime(LocalDateTime expiryTime) {
		this.expiryTime = expiryTime;
	}
    
}