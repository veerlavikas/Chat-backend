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
     * ✅ Remove a member from the group by phone number
     */
    public void leaveGroup(Long groupId, String phone) {
        ChatGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        group.getMemberPhones().remove(phone);

        // If the admin leaves, assign the next person as admin (Optional logic)
        if (phone.equals(group.getAdminPhone()) && !group.getMemberPhones().isEmpty()) {
            group.setAdminPhone(group.getMemberPhones().get(0));
        }

        groupRepo.save(group);
    }
}