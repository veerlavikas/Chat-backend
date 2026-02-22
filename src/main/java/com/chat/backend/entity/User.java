package com.chat.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true) 
    private String username;

    @Column(unique = true, nullable = false)
    private String phone; // ✅ Used for Login and Searching

    @Column(unique = true, nullable = false)
    private String email; // ✅ Used for OTPs and Google OAuth

    @Column(nullable = true) 
    private String password;

    private String profilePic;
    private String status = "Hey there! I'm using Chat App";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getProfilePic() { return profilePic; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}