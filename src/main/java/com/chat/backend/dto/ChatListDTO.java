package com.chat.backend.dto;

import java.time.LocalDateTime;

public class ChatListDTO {

    private Long userId;
    private String username;
    private String profilePic;
    private String lastMessage;
    private LocalDateTime lastMessageTime;

    public ChatListDTO(
            Long userId,
            String username,
            String profilePic,
            String lastMessage,
            LocalDateTime lastMessageTime
    ) {
        this.userId = userId;
        this.username = username;
        this.profilePic = profilePic;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }
}
