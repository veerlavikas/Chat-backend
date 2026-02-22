package com.chat.backend.dto;

import java.time.LocalDateTime;

public class ChatListDTO {
    private String phone;       // ✅ Changed from Long userId to String phone
    private Long groupId;       // For Group Chat
    
    private String username;    // User Name or Group Name
    private String profilePic;  // User Icon or Group Icon
    
    private String lastMessage;
    private LocalDateTime lastMessageTime;

    // Constructors
    public ChatListDTO() {}

    // ✅ Updated Constructor
    public ChatListDTO(String phone, String username, String profilePic, String lastMessage, LocalDateTime lastMessageTime) {
        this.phone = phone;
        this.username = username;
        this.profilePic = profilePic;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    // ✅ Getters & Setters
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getProfilePic() { return profilePic; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }
    
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    
    public LocalDateTime getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(LocalDateTime lastMessageTime) { this.lastMessageTime = lastMessageTime; }
}