package com.chat.backend.controller;

import com.chat.backend.dto.ChatListDTO;
import com.chat.backend.dto.MessageDTO;
import com.chat.backend.entity.ChatMessage;
import com.chat.backend.entity.User;
import com.chat.backend.repository.UserRepository; // ✅ Added Import
import com.chat.backend.service.ChatService;
import com.chat.backend.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors; // ✅ Added Import

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository; // ✅ Inject this for search functionality

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * ✅ SEND MESSAGE
     * Uses @AuthenticationPrincipal to get the sender's phone securely from the JWT.
     */
    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody MessageDTO dto, @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) return ResponseEntity.status(401).body("Unauthorized");

        ChatMessage msg = new ChatMessage();
        msg.setSenderPhone(currentUser.getPhone()); 
        msg.setReceiverPhone(dto.getReceiverPhone()); 
        msg.setContent(dto.getContent());
        msg.setCreatedAt(LocalDateTime.now());
        
        ChatMessage savedMsg = chatService.save(msg);

        // Notify Receiver via WebSocket
        messagingTemplate.convertAndSendToUser(
            dto.getReceiverPhone(), 
            "/queue/messages", 
            savedMsg
        );

        // 🔥 AI Bot Integration
        if ("9999".equals(dto.getReceiverPhone())) {
            new Thread(() -> {
                try {
                    String aiReplyText = geminiService.getGeminiResponse(dto.getContent());

                    ChatMessage aiMsg = new ChatMessage();
                    aiMsg.setSenderPhone("9999");
                    aiMsg.setReceiverPhone(currentUser.getPhone());
                    aiMsg.setContent(aiReplyText);
                    aiMsg.setCreatedAt(LocalDateTime.now());

                    ChatMessage savedAiMsg = chatService.save(aiMsg);

                    messagingTemplate.convertAndSendToUser(
                        currentUser.getPhone(),
                        "/queue/messages",
                        savedAiMsg
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        return ResponseEntity.ok(savedMsg);
    }

    /**
     * ✅ SEARCH USERS
     * Used for starting a new chat by searching phone numbers.
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String phone, @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) return ResponseEntity.status(401).body("Unauthorized");

        List<User> users = userRepository.findByPhoneContaining(phone)
                            .stream()
                            .filter(u -> !u.getPhone().equals(currentUser.getPhone()))
                            .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * ✅ CHAT HISTORY
     */
    @GetMapping("/history/{otherPhone}")
    public ResponseEntity<?> history(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String otherPhone,
            @RequestParam(required = false) Long groupId
    ) {
        if (currentUser == null) return ResponseEntity.status(401).body("Unauthorized");
        return ResponseEntity.ok(chatService.getHistory(currentUser.getPhone(), otherPhone, groupId));
    }

    /**
     * ✅ CHAT LIST (Inbox)
     */
    @GetMapping("/chats")
    public ResponseEntity<List<ChatListDTO>> chats(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            // ❌ Avoid: return ResponseEntity.status(401).body("Unauthorized"); 
            // This causes the mismatch because "Unauthorized" is a String.
            
            return ResponseEntity.status(401).build(); 
        }

        List<ChatListDTO> history = chatService.getChats(currentUser.getPhone());
        
        // If the list is empty, return the empty list object, not a String message
        return ResponseEntity.ok(history); 
    }
}