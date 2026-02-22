package com.chat.backend.controller;

import com.chat.backend.entity.ChatGroup;
import com.chat.backend.entity.User;
import com.chat.backend.repository.GroupRepository;
import com.chat.backend.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupRepository groupRepo;

    @Autowired
    private GroupService groupService;

    /**
     * ✅ CREATE GROUP
     */
    @PostMapping("/create")
    public ResponseEntity<?> createGroup(@RequestBody Map<String, Object> payload, 
                                         @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) return ResponseEntity.status(401).build();

        try {
            String name = (String) payload.get("name");
            String description = (String) payload.get("description");
            
            @SuppressWarnings("unchecked")
            List<String> memberPhones = (List<String>) payload.get("memberPhones");

            if (!memberPhones.contains(currentUser.getPhone())) {
                memberPhones.add(currentUser.getPhone());
            }

            ChatGroup group = new ChatGroup();
            group.setName(name);
            group.setDescription(description);
            group.setAdminPhone(currentUser.getPhone());
            group.setMemberPhones(memberPhones);

            return ResponseEntity.ok(groupRepo.save(group));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * ✅ GET GROUP MEMBERS
     * Used by GroupInfoScreen.js to show the participant list.
     */
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<User>> getMembers(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroupMembers(groupId));
    }

    /**
     * ✅ LEAVE GROUP
     */
    @PostMapping("/{groupId}/leave")
    public ResponseEntity<?> leaveGroup(@PathVariable Long groupId, @RequestBody Map<String, String> payload) {
        String phone = payload.get("phone");
        // ✅ Fixed: Method name now matches GroupService.leaveGroup
        groupService.leaveGroup(groupId, phone);
        return ResponseEntity.ok("Left group successfully");
    }

    /**
     * ✅ GET MY GROUPS
     */
    @GetMapping("/my-groups")
    public ResponseEntity<List<ChatGroup>> getMyGroups(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) return ResponseEntity.status(401).build(); 
        
        List<ChatGroup> groups = groupRepo.findGroupsByMemberPhone(currentUser.getPhone());
        return ResponseEntity.ok(groups);
    }
}