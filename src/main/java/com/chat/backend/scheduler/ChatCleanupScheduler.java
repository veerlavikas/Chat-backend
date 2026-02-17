package com.chat.backend.scheduler;

import com.chat.backend.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ChatCleanupScheduler {

    private final ChatMessageRepository repo;

    // Runs every hour
    @Scheduled(cron = "0 0 * * * *")
    public void deleteOldMessages() {
    	repo.deleteByCreatedAtBefore(
    		    LocalDateTime.now().minusHours(12)
    		);

        System.out.println("Old messages deleted.");
    }

	public ChatCleanupScheduler(ChatMessageRepository repo) {
		super();
		this.repo = repo;
	}
}
