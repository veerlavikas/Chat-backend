package com.chat.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chat_groups")
public class ChatGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Column(name = "group_icon")
    private String groupIcon; 
    
    private String description;

    // ✅ UPDATED: Identifies the admin by phone number string
    @Column(name = "admin_phone")
    private String adminPhone; 

    // ✅ UPDATED: Stores phone numbers of all members
    @ElementCollection
    @CollectionTable(name = "group_members", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "member_phone")
    private List<String> memberPhones;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public ChatGroup() {}

    // --- GETTERS & SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGroupIcon() { return groupIcon; }
    public void setGroupIcon(String groupIcon) { this.groupIcon = groupIcon; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAdminPhone() { return adminPhone; }
    public void setAdminPhone(String adminPhone) { this.adminPhone = adminPhone; }

    public List<String> getMemberPhones() { return memberPhones; }
    public void setMemberPhones(List<String> memberPhones) { this.memberPhones = memberPhones; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}