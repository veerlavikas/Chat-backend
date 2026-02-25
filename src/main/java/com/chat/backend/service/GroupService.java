package com.chat.backend.service;

import com.chat.backend.entity.ChatGroup;
import com.chat.backend.entity.User;
import com.chat.backend.repository.GroupRepository;
import com.chat.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepo;

    @Autowired
    private UserRepository userRepo;

    /**
     * ✅ Fetch full User objects for all phone numbers in the group
     */
    public List<User> getGroupMembers(Long groupId) {
        ChatGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        return group.getMemberPhones().stream()
                .map(phone -> userRepo.findByPhone(phone).orElse(null))
                .filter(user -> user != null)
                .collect(Collectors.toList());
    }

    /**
     * ✅ Remove a member (self-exit) from the group
     */
    public void leaveGroup(Long groupId, String phone) {
        ChatGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        group.getMemberPhones().remove(phone);
        
        // Remove from admin list if they were an admin
        group.getAdminPhones().remove(phone);

        // If no admins left but members remain, assign a new admin
        if (group.getAdminPhones().isEmpty() && !group.getMemberPhones().isEmpty()) {
            group.getAdminPhones().add(group.getMemberPhones().get(0));
        }

        groupRepo.save(group);
    }

    /**
     * ✅ Fetch phone numbers for WebSocket fan-out
     */
    public List<String> getGroupMemberPhones(Long groupId) {
        ChatGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        return group.getMemberPhones();
    }

    /**
     * ✅ ADD MEMBER (Admin Only) - Fixed to use adminPhones list
     */
    public void addMember(Long groupId, String requesterPhone, String newMemberPhone) {
        ChatGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // ✅ FIXED: Check the list, not a single string
        if (!group.getAdminPhones().contains(requesterPhone)) {
            throw new RuntimeException("Only the admin can add members!");
        }

        if (!group.getMemberPhones().contains(newMemberPhone)) {
            group.getMemberPhones().add(newMemberPhone);
            groupRepo.save(group);
        }
    }

    /**
     * ✅ REMOVE MEMBER (Admin Only) - Fixed to use adminPhones list
     */
    public void removeMember(Long groupId, String requesterPhone, String memberToRemove) {
        ChatGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // ✅ FIXED: Check the list
        if (!group.getAdminPhones().contains(requesterPhone)) {
            throw new RuntimeException("Only the admin can remove members!");
        }

        group.getMemberPhones().remove(memberToRemove);
        group.getAdminPhones().remove(memberToRemove); // Also remove from admin list if they were one
        groupRepo.save(group);
    }

    /**
     * ✅ PROMOTE TO ADMIN
     */
    public void promoteToAdmin(Long groupId, String requesterPhone, String targetPhone) {
        ChatGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        if (!group.getAdminPhones().contains(requesterPhone)) {
            throw new RuntimeException("Only admins can promote!");
        }
        
        if (!group.getAdminPhones().contains(targetPhone)) {
            group.getAdminPhones().add(targetPhone);
            groupRepo.save(group);
        }
    }

    /**
     * ✅ UPDATE GROUP (Name / Icon)
     */
    public void updateGroupDetails(Long groupId, String requesterPhone, String newName, String newIcon) {
        ChatGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        if (!group.getAdminPhones().contains(requesterPhone)) {
            throw new RuntimeException("Only admins can edit!");
        }
        
        if (newName != null && !newName.isBlank()) group.setName(newName);
        if (newIcon != null && !newIcon.isBlank()) group.setGroupIcon(newIcon);
        groupRepo.save(group);
    }

    /**
     * ✅ DELETE GROUP (Admin Only)
     */
    public void deleteGroup(Long groupId, String requesterPhone) {
        ChatGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // ✅ FIXED: Now correctly checks the list
        if (!group.getAdminPhones().contains(requesterPhone)) {
            throw new RuntimeException("Only admins can delete the group!");
        }
        
        groupRepo.delete(group);
    }
    /**
     * ✅ DEMOTE ADMIN (Dismiss as admin)
     */
    public void demoteAdmin(Long groupId, String requesterPhone, String targetPhone) {
        ChatGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!group.getAdminPhones().contains(requesterPhone)) {
            throw new RuntimeException("Only admins can dismiss other admins!");
        }

        // Prevent admins from demoting themselves by accident
        if (requesterPhone.equals(targetPhone)) {
            throw new RuntimeException("You cannot dismiss yourself!");
        }

        group.getAdminPhones().remove(targetPhone);
        groupRepo.save(group);
    }
}