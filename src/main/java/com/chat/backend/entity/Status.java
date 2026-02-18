package com.chat.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_status")
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // Who posted this?
    private String username; // Store name to display quickly
    private String userProfilePic; 

    private String mediaUrl; // Image or Video URL
    private String caption;
    private String type; // "IMAGE" or "VIDEO"

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public Status() {
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusHours(24); // Auto-expire logic
    }

    // Getters and Setters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    // Add other getters/setters as needed

	public  String getUsername() {
		return username;
	}

	public  void setUsername(String username) {
		this.username = username;
	}

	public  String getUserProfilePic() {
		return userProfilePic;
	}

	public  void setUserProfilePic(String userProfilePic) {
		this.userProfilePic = userProfilePic;
	}

	public  String getCaption() {
		return caption;
	}

	public  void setCaption(String caption) {
		this.caption = caption;
	}

	public  String getType() {
		return type;
	}

	public  void setType(String type) {
		this.type = type;
	}

	public  LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public  void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public  void setId(Long id) {
		this.id = id;
	}

	public  void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}
}