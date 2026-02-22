package com.chat.backend.service;

import com.chat.backend.entity.Status;
import com.chat.backend.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatusCleanupService {

    @Autowired
    private StatusRepository statusRepo;

    // Use the same path your MediaService uses
    private final String uploadDir = "uploads/media";

    /**
     * ✅ Runs every hour
     * Deletes physical files AND database records for expired statuses.
     */
    @Scheduled(fixedRate = 3600000) 
    @Transactional
    public void purgeExpiredStatuses() {
        LocalDateTime now = LocalDateTime.now();
        
        // 1. Find which statuses are about to be deleted
        List<Status> expiredStatuses = statusRepo.findByExpiresAtBefore(now);
        
        if (!expiredStatuses.isEmpty()) {
            for (Status status : expiredStatuses) {
                deletePhysicalFile(status.getMediaUrl());
            }

            // 2. Clear from Database
            statusRepo.deleteAll(expiredStatuses);
            System.out.println("Cleanup: Purged " + expiredStatuses.size() + " expired statuses and their files.");
        }
    }

    private void deletePhysicalFile(String mediaUrl) {
        try {
            // Extract filename from the URL (e.g., https://.../filename.jpg -> filename.jpg)
            String fileName = mediaUrl.substring(mediaUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            File file = filePath.toFile();

            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("Deleted file: " + fileName);
                } else {
                    System.err.println("Failed to delete file: " + fileName);
                }
            }
        } catch (Exception e) {
            System.err.println("Error deleting file for URL " + mediaUrl + ": " + e.getMessage());
        }
    }
}