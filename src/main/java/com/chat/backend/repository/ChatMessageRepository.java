package com.chat.backend.repository;

import com.chat.backend.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Chat history
    @Query(
        "SELECT m FROM ChatMessage m " +
        "WHERE (m.senderId = :sender AND m.receiverId = :receiver) " +
        "   OR (m.senderId = :receiver AND m.receiverId = :sender) " +
        "ORDER BY m.createdAt ASC"
    )
    List<ChatMessage> findChatHistory(
            @Param("sender") Long sender,
            @Param("receiver") Long receiver
    );

    // 🔥 WhatsApp-style chat list
    @Query(
        "SELECT m FROM ChatMessage m " +
        "WHERE m.id IN ( " +
        "  SELECT MAX(c.id) FROM ChatMessage c " +
        "  WHERE c.senderId = :me OR c.receiverId = :me " +
        "  GROUP BY CASE " +
        "    WHEN c.senderId = :me THEN c.receiverId " +
        "    ELSE c.senderId " +
        "  END " +
        ") " +
        "ORDER BY m.createdAt DESC"
    )
    List<ChatMessage> findChatList(@Param("me") Long me);

    void deleteByCreatedAtBefore(LocalDateTime time);
}
