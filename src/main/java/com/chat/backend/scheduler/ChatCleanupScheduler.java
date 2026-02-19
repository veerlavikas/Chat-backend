package com.chat.backend.scheduler;

import com.chat.backend.repository.ChatMessageRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // ✅ 1. Add this import

import java.time.LocalDateTime;

@Component
public class ChatCleanupScheduler {

    private final ChatMessageRepository repo;

    // Manual constructor - This is the most reliable way for cloud deployment
    public ChatCleanupScheduler(ChatMessageRepository repo) {
        this.repo = repo;
    }

    // Runs every hour
    @Scheduled(cron = "0 0 * * * *")
    @Transactional // ✅ 2. ADD THIS ANNOTATION HERE!
    public void deleteOldMessages() {
        repo.deleteByCreatedAtBefore(
            LocalDateTime.now().minusHours(12)
        );

        System.out.println("Old messages deleted.");
    }
}