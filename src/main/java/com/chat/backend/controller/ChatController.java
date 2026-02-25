package com.chat.backend.controller;

import com.chat.backend.dto.ChatListDTO;
import com.chat.backend.dto.MessageDTO;
import com.chat.backend.entity.ChatMessage;
import com.chat.backend.entity.User;
import com.chat.backend.repository.ChatMessageRepository;
import com.chat.backend.repository.UserRepository;
import com.chat.backend.service.ChatService;
import com.chat.backend.service.GeminiService;
import com.chat.backend.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // ✅ INJECTED GROUP SERVICE FOR FAN-OUT MAGIC
    @Autowired
    private GroupService groupService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private ChatMessageRepository chatRepo;

    /**
     * ✅ SEND MESSAGE (HANDLES 1-ON-1, GROUPS, AND MEDIA)
     */
    @PostMapping("/send")
    public ResponseEntity<?> send(
            @RequestBody MessageDTO dto,
            @AuthenticationPrincipal User currentUser
    ) {
        System.out.println("📩 /api/chat/send HIT");

        if (currentUser == null) {
            System.out.println("❌ Unauthorized user");
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // ✅ 1. VALIDATION: Must have either a receiver or a group
        if ((dto.getReceiverPhone() == null || dto.getReceiverPhone().isBlank()) && dto.getGroupId() == null) {
            return ResponseEntity.badRequest().body("receiverPhone or groupId is required");
        }

        // ✅ 2. VALIDATION: Must have text or media
        if ((dto.getContent() == null || dto.getContent().isBlank()) && 
            (dto.getMediaUrl() == null || dto.getMediaUrl().isBlank())) {
            return ResponseEntity.badRequest().body("Message content or media is required");
        }

        // 💾 3. CREATE & POPULATE MESSAGE
        ChatMessage msg = new ChatMessage();
        msg.setSenderPhone(currentUser.getPhone());
        User realUser = userRepository.findByPhone(currentUser.getPhone()).orElse(currentUser);
        String actualName = (realUser.getUsername() != null && !realUser.getUsername().isBlank()) 
                            ? realUser.getUsername() 
                            : realUser.getPhone();
                            
        msg.setSenderName(actualName);
        msg.setContent(dto.getContent());
        msg.setType(dto.getType());
        msg.setMediaUrl(dto.getMediaUrl());
        msg.setCreatedAt(LocalDateTime.now());

        // Assign to Group OR Private Chat
        if (dto.getGroupId() != null) {
            msg.setGroupId(dto.getGroupId());
        } else {
            msg.setReceiverPhone(dto.getReceiverPhone());
        }

        // Save to Database
        ChatMessage savedMsg = chatService.save(msg);
        System.out.println("✅ Message saved with ID: " + savedMsg.getId());

        // 📡 4. WEBSOCKET BROADCASTER (THE FAN-OUT)
        if (dto.getGroupId() != null) {
            System.out.println("🌐 Broadcasting to Group: " + dto.getGroupId());
            // Fetch all phones in the group from our new GroupService method!
            List<String> memberPhones = groupService.getGroupMemberPhones(dto.getGroupId());
            
            for (String memberPhone : memberPhones) {
                // Send to everyone EXCEPT the sender
                if (!memberPhone.equals(currentUser.getPhone())) {
                    messagingTemplate.convertAndSend("/topic/chat/" + memberPhone, savedMsg);
                }
            }
        } else {
            System.out.println("👤 Broadcasting to User: " + dto.getReceiverPhone());
            // 1-on-1 Chat Broadcast
            messagingTemplate.convertAndSend("/topic/chat/" + dto.getReceiverPhone(), savedMsg);
        }

        // 🤖 5. AI BOT LOGIC (Remains exactly the same)
        if ("9999".equals(dto.getReceiverPhone())) {
            new Thread(() -> {
                try {
                    String prompt = dto.getContent() != null ? dto.getContent() : "User sent an attachment.";
                    String aiReply = geminiService.getGeminiResponse(prompt);

                    ChatMessage aiMsg = new ChatMessage();
                    aiMsg.setSenderPhone("9999");
                    aiMsg.setReceiverPhone(currentUser.getPhone());
                    aiMsg.setContent(aiReply);
                    aiMsg.setType("text");
                    aiMsg.setCreatedAt(LocalDateTime.now());

                    ChatMessage savedAi = chatService.save(aiMsg);
                    messagingTemplate.convertAndSend("/topic/chat/" + currentUser.getPhone(), savedAi);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        return ResponseEntity.ok(savedMsg);
    }
     
    /**
     * ✅ FETCH 1-ON-1 CHAT HISTORY
     */
    @GetMapping("/history/{otherPhone}")
    public ResponseEntity<?> history(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String otherPhone
    ) {
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return ResponseEntity.ok(
                chatService.getHistory(currentUser.getPhone(), otherPhone, null)
        );
    }

    /**
     * ✅ FETCH RECENT CHATS LIST
     */
    @GetMapping("/chats")
    public ResponseEntity<List<ChatListDTO>> chats(
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(chatService.getChats(currentUser.getPhone()));
    }
    /**
     * ✅ FETCH GROUP CHAT HISTORY
     */
    @GetMapping("/group-history/{groupId}")
    public ResponseEntity<?> groupHistory(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long groupId
    ) {
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        
        // Tells the ChatService to fetch messages where groupId matches
        return ResponseEntity.ok(
                chatService.getHistory(null, null, groupId)
        );
    }
    /**
     * 🔥 CLEAR CHAT HISTORY (Used for Meta AI cleanup)
     */
    @DeleteMapping("/clear/{contactPhone}")
    public ResponseEntity<?> clearChatHistory(@PathVariable String contactPhone, @AuthenticationPrincipal User currentUser) {
        try {
            // This calls the exact @Modifying @Query you just built in the repository!
            chatRepo.deleteChatHistory(currentUser.getPhone(), contactPhone);
            
            return ResponseEntity.ok("Chat history cleared for " + contactPhone);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error clearing chat: " + e.getMessage());
        }
    }
}