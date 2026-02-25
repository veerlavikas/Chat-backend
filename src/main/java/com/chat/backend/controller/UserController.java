package com.chat.backend.controller;

import com.chat.backend.entity.User;
import com.chat.backend.repository.UserRepository;
import com.chat.backend.service.MediaService;
import com.chat.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserService userService;
    
    @Autowired
    private MediaService mediaService;

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        
        // Update username if provided
        String newUsername = body.get("username");
        if (newUsername != null && !newUsername.trim().isEmpty()) {
            user.setUsername(newUsername);
        }

        // 🔥 FIX: Actually save the Cloudinary URL to the database!
        String newProfilePic = body.get("profilePic");
        if (newProfilePic != null && !newProfilePic.trim().isEmpty()) {
            user.setProfilePic(newProfilePic);
        }

        // Keep status updated too if you pass it
        String newStatus = body.get("status");
        if (newStatus != null && !newStatus.trim().isEmpty()) {
            user.setStatus(newStatus);
        }

        userRepo.save(user); 
        
        return ResponseEntity.ok(Map.of(
                "message", "Profile updated successfully",
                "user", user // Send back the whole updated user object
        ));
    }
    @PostMapping("/upload-dp")
    public ResponseEntity<?> uploadProfilePic(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user
    ) throws IOException {

        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // 🔥 Upload to Cloudinary via MediaService
        String imageUrl = mediaService.saveFile(file);

        // 🔥 Save FULL URL in DB
        user.setProfilePic(imageUrl);
        userRepo.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "Profile picture updated",
                "profilePic", imageUrl
        ));
    }
    @GetMapping("/by-phone/{phone}")
    public ResponseEntity<?> getUserByPhone(@PathVariable String phone) {
        String normalizedPhone = phone.replace("+91", "").trim(); 
        
        Optional<User> user = userRepo.findByPhone(normalizedPhone);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepo.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String q) {
        String query = q.replaceAll("\\s+", "").replace("+91", "");
        
        return ResponseEntity.ok(
            userRepo.findByUsernameContainingIgnoreCaseOrPhoneContaining(query, query)
        );
    }
}