package com.chat.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    // Optional for Group Chats
    @Column(name = "receiver_id")
    private Long receiverId;

    // ✅ NEW: Supports Group Messaging
    @Column(name = "group_id")
    private Long groupId;

    @Column(length = 1000)
    private String content;

    @Column(name = "media_url")
    private String mediaUrl;

    // ✅ NEW: Message Type (TEXT, IMAGE, DOC, etc.)
    private String type = "TEXT";

    // ✅ UPDATED: Replaces 'seen' with WhatsApp-style status 
    // (0=Sending, 1=Sent, 2=Delivered, 3=Seen)
    @Column(nullable = false)
    private int status = 1; 

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public ChatMessage() {}

    // --- GETTERS & SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}