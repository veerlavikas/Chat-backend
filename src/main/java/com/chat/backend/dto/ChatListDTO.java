package com.chat.backend.dto;

import java.time.LocalDateTime;

public class ChatListDTO {
    private Long userId;        // For Private Chat
    private Long groupId;       // ✅ NEW: For Group Chat
    
    private String username;    // User Name or Group Name
    private String profilePic;  // User Icon or Group Icon
    
    private String lastMessage;
    private LocalDateTime lastMessageTime;

    // Constructors
    public ChatListDTO() {}

    public ChatListDTO(Long userId, String username, String profilePic, String lastMessage, LocalDateTime lastMessageTime) {
        this.userId = userId;
        this.username = username;
        this.profilePic = profilePic;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    // ✅ Getters & Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    
    public String getUsername() { return username; } // This will hold GroupName if it's a group
    public void setUsername(String username) { this.username = username; }
    
    public String getProfilePic() { return profilePic; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }
    
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    
    public LocalDateTime getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(LocalDateTime lastMessageTime) { this.lastMessageTime = lastMessageTime; }
}