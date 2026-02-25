package com.chat.backend.repository;

import com.chat.backend.entity.ChatMessage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // ✅ 1. Updated Chat History: Using Phone Numbers (Strings)
    @Query("SELECT m FROM ChatMessage m WHERE " +
           "(:receiverPhone IS NULL AND m.groupId = :groupId) OR " +
           "(m.groupId IS NULL AND ((m.senderPhone = :senderPhone AND m.receiverPhone = :receiverPhone) OR " +
           "(m.senderPhone = :receiverPhone AND m.receiverPhone = :senderPhone))) " +
           "ORDER BY m.createdAt ASC")
    List<ChatMessage> findChatHistory(
            @Param("senderPhone") String senderPhone,
            @Param("receiverPhone") String receiverPhone,
            @Param("groupId") Long groupId
    );

    // ✅ 2. Updated Chat List Query: Handles Private (Phone) + Group (ID)
    @Query("SELECT m FROM ChatMessage m WHERE m.id IN (" +
           "  SELECT MAX(c.id) FROM ChatMessage c " +
           "  WHERE c.senderPhone = :me OR c.receiverPhone = :me OR c.groupId IN :groupIds " +
           "  GROUP BY COALESCE(CAST(c.groupId AS string), CASE WHEN c.senderPhone = :me THEN c.receiverPhone ELSE c.senderPhone END)" +
           ") ORDER BY m.createdAt DESC")
    List<ChatMessage> findChatList(@Param("me") String me, @Param("groupIds") List<Long> groupIds);

    // ✅ 3. Find latest message in a group (Remains Long as Groups have numeric IDs)
    ChatMessage findTopByGroupIdOrderByCreatedAtDesc(Long groupId);

    // ✅ 4. Support for 'Seen' Status using Phone Numbers
    List<ChatMessage> findBySenderPhoneAndReceiverPhoneAndStatusNot(String senderPhone, String receiverPhone, int status);
    
    // ✅ 5. Support for ChatService logic (Phone-based)
    List<ChatMessage> findBySenderPhoneOrReceiverPhone(String senderPhone, String receiverPhone);
    
    List<ChatMessage> findByGroupIdOrderByCreatedAtAsc(Long groupId);

    // ✅ 6. Cleanup task
    void deleteByCreatedAtBefore(LocalDateTime time);
    @Modifying
    @Transactional
    @Query("DELETE FROM ChatMessage c WHERE (c.senderPhone = :p1 AND c.receiverPhone = :p2) OR (c.senderPhone = :p2 AND c.receiverPhone = :p1)")
    void deleteChatHistory(String p1, String p2);
}