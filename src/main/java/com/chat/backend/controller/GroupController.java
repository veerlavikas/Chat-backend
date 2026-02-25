package com.chat.backend.controller;

import com.chat.backend.entity.ChatGroup;
import com.chat.backend.entity.User;
import com.chat.backend.repository.GroupRepository;
import com.chat.backend.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
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
    /**
     * ✅ LEAVE GROUP (SECURE WAY)
     */
    @PostMapping("/{groupId}/leave")
    public ResponseEntity<?> leaveGroup(@PathVariable Long groupId, @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        
        // Securely use the token's phone number! No spoofing allowed.
        groupService.leaveGroup(groupId, currentUser.getPhone());
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
    /**
     * ✅ GET FULL GROUP INFO (Includes Admin info)
     */
    /**
     * ✅ GET FULL GROUP INFO (Fixed to send 'myPhone')
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroupInfo(@PathVariable Long groupId, @AuthenticationPrincipal User currentUser) {
        ChatGroup group = groupRepo.findById(groupId).orElse(null);
        if (group == null) return ResponseEntity.notFound().build();
        
        List<User> members = groupService.getGroupMembers(groupId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("group", group);
        response.put("members", members);
        
        // 🔥 FOOLPROOF FIX: The backend explicitly tells the frontend who is asking!
        response.put("myPhone", currentUser.getPhone()); 
        
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ CREATE GROUP (Fixed to save the admin list!)
     */
    @PostMapping("/create")
    public ResponseEntity<?> createGroup(@RequestBody Map<String, Object> payload, @AuthenticationPrincipal User currentUser) {
        try {
            String name = (String) payload.get("name");
            String description = (String) payload.get("description");
            
            @SuppressWarnings("unchecked")
            List<String> memberPhones = (List<String>) payload.get("memberPhones");

            // Make sure creator is in the members list
            if (!memberPhones.contains(currentUser.getPhone())) {
                memberPhones.add(currentUser.getPhone());
            }

            ChatGroup group = new ChatGroup();
            group.setName(name);
            group.setDescription(description);
            group.setMemberPhones(memberPhones);
            
            // 🔥 CRITICAL FIX: Save the creator into the new adminPhones list!
            List<String> admins = new ArrayList<>();
            admins.add(currentUser.getPhone());
            group.setAdminPhones(admins);

            return ResponseEntity.ok(groupRepo.save(group));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    /**
     * ✅ ADD MEMBER API
     */
    @PostMapping("/{groupId}/add")
    public ResponseEntity<?> addMember(@PathVariable Long groupId, @RequestBody Map<String, String> payload, @AuthenticationPrincipal User user) {
        try {
            groupService.addMember(groupId, user.getPhone(), payload.get("phone"));
            return ResponseEntity.ok("Member added");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * ✅ REMOVE MEMBER API
     */
    @PostMapping("/{groupId}/remove")
    public ResponseEntity<?> removeMember(@PathVariable Long groupId, @RequestBody Map<String, String> payload, @AuthenticationPrincipal User user) {
        try {
            groupService.removeMember(groupId, user.getPhone(), payload.get("phone"));
            return ResponseEntity.ok("Member removed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/{groupId}/make-admin")
    public ResponseEntity<?> makeAdmin(@PathVariable Long groupId, @RequestBody Map<String, String> payload, @AuthenticationPrincipal User user) {
        groupService.promoteToAdmin(groupId, user.getPhone(), payload.get("phone"));
        return ResponseEntity.ok("Promoted to Admin");
    }

    @PutMapping("/{groupId}/update")
    public ResponseEntity<?> updateGroup(@PathVariable Long groupId, @RequestBody Map<String, String> payload, @AuthenticationPrincipal User user) {
        groupService.updateGroupDetails(groupId, user.getPhone(), payload.get("name"), payload.get("icon"));
        return ResponseEntity.ok("Group updated");
    }

    @DeleteMapping("/{groupId}/delete")
    public ResponseEntity<?> deleteGroup(@PathVariable Long groupId, @AuthenticationPrincipal User user) {
        groupService.deleteGroup(groupId, user.getPhone());
        return ResponseEntity.ok("Group deleted");
    }
    /**
     * ✅ DISMISS AS ADMIN
     */
    @PostMapping("/{groupId}/dismiss-admin")
    public ResponseEntity<?> dismissAdmin(@PathVariable Long groupId, @RequestBody Map<String, String> payload, @AuthenticationPrincipal User user) {
        try {
            groupService.demoteAdmin(groupId, user.getPhone(), payload.get("phone"));
            return ResponseEntity.ok("Dismissed as Admin");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}