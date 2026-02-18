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
    @PostMapping("/upload")
    public ResponseEntity<?> uploadStatus(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam("username") String username
    ) {
        try {
            String url = mediaService.saveFile(file); // Uses your existing MediaService
            
            Status status = new Status();
            status.setUserId(userId);
            status.setUsername(username); // Store username to display easily
            status.setMediaUrl(url);
            status.setType("IMAGE");
            
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
}