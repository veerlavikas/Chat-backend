package com.chat.backend.repository;

import com.chat.backend.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // ✅ 1. Updated Chat History: Support for both Private and Group history
    @Query("SELECT m FROM ChatMessage m WHERE " +
           "(:receiver IS NULL AND m.groupId = :groupId) OR " +
           "(m.groupId IS NULL AND ((m.senderId = :sender AND m.receiverId = :receiver) OR (m.senderId = :receiver AND m.receiverId = :sender))) " +
           "ORDER BY m.createdAt ASC")
    List<ChatMessage> findChatHistory(
            @Param("sender") Long sender,
            @Param("receiver") Long receiver,
            @Param("groupId") Long groupId
    );

    // ✅ 2. Updated Chat List Query (Private + Group + Bot)
    // This looks for the latest message in every unique conversation you are part of
    @Query("SELECT m FROM ChatMessage m WHERE m.id IN (" +
           "  SELECT MAX(c.id) FROM ChatMessage c " +
           "  WHERE c.senderId = :me OR c.receiverId = :me OR c.groupId IN :groupIds " +
           "  GROUP BY COALESCE(c.groupId, CASE WHEN c.senderId = :me THEN c.receiverId ELSE c.senderId END)" +
           ") ORDER BY m.createdAt DESC")
    List<ChatMessage> findChatList(@Param("me") Long me, @Param("groupIds") List<Long> groupIds);

    // ✅ 3. Find the very last message in a specific group
    ChatMessage findTopByGroupIdOrderByCreatedAtDesc(Long groupId);

    // ✅ 4. Cleanup for Statuses/Temporary data
    void deleteByCreatedAtBefore(LocalDateTime time);

    // ✅ 5. Support for 'Seen' Status (WhatsApp Ticks)
    List<ChatMessage> findBySenderIdAndReceiverIdAndStatusNot(Long senderId, Long receiverId, int status);
    
    // For your ChatService logic mentioned earlier
    List<ChatMessage> findBySenderIdOrReceiverId(Long senderId, Long receiverId);
}