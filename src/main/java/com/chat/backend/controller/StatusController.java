package com.chat.backend.controller;

import com.chat.backend.entity.Status;
import com.chat.backend.entity.StatusComment;
import com.chat.backend.repository.StatusCommentRepository;
import com.chat.backend.repository.StatusRepository;
import com.chat.backend.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/status")
@CrossOrigin("*")
public class StatusController {

    @Autowired private StatusRepository statusRepo;
    @Autowired private StatusCommentRepository commentRepo;
    @Autowired private MediaService mediaService;

    // 1. Upload Status
 // 1. Upload Status (with 24h expiration)
    @PostMapping("/upload")
    public ResponseEntity<?> uploadStatus(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam("username") String username
    ) {
        try {
            String url = mediaService.saveFile(file);
            
            Status status = new Status();
            status.setUserId(userId);
            status.setUsername(username);
            status.setMediaUrl(url);
            status.setType("IMAGE");
            // ✅ Set expiration to 24 hours from now
            status.setExpiresAt(LocalDateTime.now().plusHours(24));
            
            statusRepo.save(status);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    
    // 2. Get All Active Statuses (Last 24h)
    @GetMapping("/active")
    public List<Status> getActiveStatuses() {
        return statusRepo.findByExpiresAtAfter(LocalDateTime.now());
    }

    // 3. Add Comment
    @PostMapping("/{statusId}/comment")
    public StatusComment addComment(@PathVariable Long statusId, @RequestBody StatusComment comment) {
        comment.setStatusId(statusId);
        return commentRepo.save(comment);
    }

    // 4. Get Comments for a Status
    @GetMapping("/{statusId}/comments")
    public List<StatusComment> getComments(@PathVariable Long statusId) {
        return commentRepo.findByStatusId(statusId);
    }
 // 5. Delete Status (Requested for Edit Option)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStatus(@PathVariable Long id) {
        try {
            // You can also add logic here to call mediaService.deleteFile() 
            // to remove the physical image from your /uploads folder.
            statusRepo.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Delete failed: " + e.getMessage());
        }
    }

    // 6. Get My Own Status
    @GetMapping("/mine/{userId}")
    public ResponseEntity<?> getMyStatus(@PathVariable Long userId) {
        List<Status> myStatuses = statusRepo.findByUserIdAndExpiresAtAfter(userId, LocalDateTime.now());
        return ResponseEntity.ok(myStatuses);
    }
}