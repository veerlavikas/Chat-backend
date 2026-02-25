package com.chat.backend.dto;

public class MessageDTO {

    private String senderPhone;
    
    // 🔥 ADDED THIS SO THE FRONTEND CAN RECEIVE THE NAME
    private String senderName; 
    
    private String receiverPhone; 
    private String content;
    private Long groupId;         
    
    // ✅ NEW FIELDS FOR ATTACHMENTS
    private String type;
    private String mediaUrl;

    // Getters and Setters
    public String getSenderPhone() { return senderPhone; }
    public void setSenderPhone(String senderPhone) { this.senderPhone = senderPhone; }

    // 🔥 NEW GETTER & SETTER FOR SENDER NAME
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
}