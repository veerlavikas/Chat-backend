package com.chat.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages") // Standardizing table name
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ UPDATED: Changed from Long to String to store Phone Numbers
    @Column(name = "sender_phone", nullable = false)
    private String senderPhone;

    // ✅ UPDATED: Changed from Long to String
    @Column(name = "receiver_phone")
    private String receiverPhone;

    // Supports Group Messaging (Kept as Long for Group ID reference)
    @Column(name = "group_id")
    private Long groupId;

    @Column(length = 2000) // Increased length for longer AI responses or encrypted text
    private String content;

    @Column(name = "media_url")
    private String mediaUrl;

    private String type = "TEXT"; // TEXT, IMAGE, AUDIO, VIDEO

    // WhatsApp-style status (0=Sending, 1=Sent, 2=Delivered, 3=Seen)
    @Column(nullable = false)
    private int status = 1; 

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public ChatMessage() {}

    // --- GETTERS & SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSenderPhone() { return senderPhone; }
    public void setSenderPhone(String senderPhone) { this.senderPhone = senderPhone; }

    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }

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