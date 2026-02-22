package com.chat.backend.dto;

public class MessageDTO {

    private String senderPhone;   // ✅ Changed from Long to String
    private String receiverPhone; // ✅ Changed from Long to String
    private String content;
    private Long groupId;         // ✅ Added for Group Chat support

    // Getters and Setters
    public String getSenderPhone() {
        return senderPhone;
    }
    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }
    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public Long getGroupId() {
        return groupId;
    }
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}