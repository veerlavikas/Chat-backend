package com.chat.backend.controller;

import com.chat.backend.entity.ChatGroup;
import com.chat.backend.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupRepository groupRepo;

    @PostMapping("/create")
    public ResponseEntity<?> createGroup(@RequestBody Map<String, Object> payload) {
        try {
            String name = (String) payload.get("name");
            Long adminId = Long.valueOf(payload.get("adminId").toString());
            
            // ✅ Convert List<Integer> from JSON to List<Long>
            List<Integer> memberIdsRaw = (List<Integer>) payload.get("memberIds");
            List<Long> memberIds = memberIdsRaw.stream()
                    .map(id -> Long.valueOf(id.toString()))
                    .collect(Collectors.toList());

            ChatGroup group = new ChatGroup();
            group.setName(name);
            group.setAdminId(adminId);
            group.setMemberIds(memberIds); // Works with your @ElementCollection

            return ResponseEntity.ok(groupRepo.save(group));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // ✅ Get groups for the user's chat list
    @GetMapping("/my-groups/{userId}")
    public ResponseEntity<List<ChatGroup>> getMyGroups(@PathVariable Long userId) {
        return ResponseEntity.ok(groupRepo.findMyGroups(userId));
    }
}