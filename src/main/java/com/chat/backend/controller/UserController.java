package com.chat.backend.controller;

import com.chat.backend.entity.User;
import com.chat.backend.repository.UserRepository;
import com.chat.backend.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserService userService;

    // ✅ Upload / Update Profile Picture
    @PostMapping("/upload-dp")
    public ResponseEntity<?> uploadProfilePic(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token
    ) throws IOException {

        User user = userService.getUserFromToken(token);

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get("uploads/profile");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        user.setProfilePic(fileName);
        userService.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "Profile picture updated",
                "profilePic", fileName
        ));
    }

    // 🔎 Find user by phone number (for new chat)
    @GetMapping("/by-phone/{phone}")
    public ResponseEntity<?> getUserByPhone(@PathVariable String phone) {

        User user = userRepo.findByPhone(phone);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }

    // ✅ Get my profile (used by Settings screen)
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(
            @RequestHeader("Authorization") String token
    ) {
        User user = userService.getUserFromToken(token);
        return ResponseEntity.ok(user);
    }
 // 🔍 Search users by name or phone (New Chat)
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String q) {

        // normalize phone (optional but recommended)
        String query = q.replaceAll("\\s+", "").replace("+91", "");

        return ResponseEntity.ok(
            userRepo.findByUsernameContainingIgnoreCaseOrPhoneContaining(
                query, query
            )
        );
    }
 // ✅ NEW: Get all users (used for Create Group and Contacts list)
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        // This returns every user in your TiDB database
        return ResponseEntity.ok(userRepo.findAll());
    }

}
