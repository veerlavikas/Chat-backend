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

    /**
     * ✅ UPLOAD STATUS (Phone-based)
     * Sets expiration to 24 hours.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadStatus(
            @RequestParam("file") MultipartFile file,
            @RequestParam("phone") String phone, // ✅ Changed to String phone
            @RequestParam("username") String username
    ) {
        try {
            String url = mediaService.saveFile(file);
            
            Status status = new Status();
            status.setPhone(phone); // ✅ Updated field
            status.setUsername(username);
            status.setMediaUrl(url);
            status.setType("IMAGE");
            status.setCreatedAt(LocalDateTime.now());
            status.setExpiresAt(LocalDateTime.now().plusHours(24));
            
            statusRepo.save(status);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * ✅ GET ACTIVE STATUSES
     * Returns all updates that haven't reached their 24h limit.
     */
    @GetMapping("/active")
    public List<Status> getActiveStatuses() {
        return statusRepo.findByExpiresAtAfter(LocalDateTime.now());
    }

    /**
     * ✅ ADD COMMENT
     */
    @PostMapping("/{statusId}/comment")
    public StatusComment addComment(@PathVariable Long statusId, @RequestBody StatusComment comment) {
        comment.setStatusId(statusId);
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepo.save(comment);
    }

    /**
     * ✅ GET COMMENTS
     */
    @GetMapping("/{statusId}/comments")
    public List<StatusComment> getComments(@PathVariable Long statusId) {
        return commentRepo.findByStatusId(statusId);
    }

    /**
     * ✅ DELETE STATUS
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStatus(@PathVariable Long id) {
        try {
            statusRepo.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Delete failed");
        }
    }

    /**
     * ✅ GET MY STATUS (Phone-based)
     */
    @GetMapping("/mine/{phone}")
    public ResponseEntity<?> getMyStatus(@PathVariable String phone) {
        List<Status> myStatuses = statusRepo.findByPhoneAndExpiresAtAfter(phone, LocalDateTime.now());
        return ResponseEntity.ok(myStatuses);
    }
}