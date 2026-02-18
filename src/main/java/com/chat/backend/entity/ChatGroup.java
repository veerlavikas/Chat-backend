package com.chat.backend.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "chat_groups")
public class ChatGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String groupIcon; // URL to the group image
    private String description;

    @Column(name = "admin_id")
    private Long adminId; // The user who created the group

    // Stores IDs of all members (e.g., [1, 5, 12])
    @ElementCollection
    private List<Long> memberIds;

    public ChatGroup() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGroupIcon() { return groupIcon; }
    public void setGroupIcon(String groupIcon) { this.groupIcon = groupIcon; }
    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }
    public List<Long> getMemberIds() { return memberIds; }
    public void setMemberIds(List<Long> memberIds) { this.memberIds = memberIds; }

	public  String getDescription() {
		return description;
	}

	public  void setDescription(String description) {
		this.description = description;
	}
    
}