package com.chat.backend.controller;

import com.chat.backend.entity.User;
import com.chat.backend.repository.UserRepository;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserService userService;

    // ✅ Get my profile (Firebase Verified)
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        return ResponseEntity.ok(user);
    }

    // ✅ NEW: Update Username (Used by ProfileSetupScreen)
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        
        String newUsername = body.get("username");
        if (newUsername == null || newUsername.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username cannot be empty");
        }

        user.setUsername(newUsername);
        userRepo.save(user); // Persistence handles the TiDB update
        
        return ResponseEntity.ok(Map.of(
                "message", "Username updated successfully",
                "username", newUsername
        ));
    }

    // ✅ Upload / Update Profile Picture
    @PostMapping("/upload-dp")
    public ResponseEntity<?> uploadProfilePic(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user
    ) throws IOException {

        if (user == null) return ResponseEntity.status(401).body("Unauthorized");

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get("uploads/profile");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        user.setProfilePic(fileName);
        userRepo.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "Profile picture updated",
                "profilePic", fileName
        ));
    }

    // 🔎 Find user by phone number
    @GetMapping("/by-phone/{phone}")
    public ResponseEntity<?> getUserByPhone(@PathVariable String phone) {
        String normalized = phone.replace("+91", "").trim();
        User user = userRepo.findByPhone(normalized);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    // ✅ Get all users for Group Creation
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepo.findAll());
    }

    // 🔍 Search users
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String q) {
        String query = q.replaceAll("\\s+", "").replace("+91", "");
        return ResponseEntity.ok(
            userRepo.findByUsernameContainingIgnoreCaseOrPhoneContaining(query, query)
        );
    }
}