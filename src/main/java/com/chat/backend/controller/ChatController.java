package com.chat.backend.controller;

import com.chat.backend.dto.ChatListDTO;
import com.chat.backend.dto.MessageDTO;
import com.chat.backend.entity.ChatMessage;
import com.chat.backend.service.ChatService;
import com.chat.backend.service.GeminiService; // ✅ Import this

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate; // ✅ Import for WebSocket
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private GeminiService geminiService; // ✅ Inject AI Service

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // ✅ Inject WebSocket Template

    // ✅ SEND MESSAGE (Updated with AI Logic)
    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody MessageDTO dto) {

        // 1. Save User's Message Normally
        ChatMessage msg = new ChatMessage();
        msg.setSenderId(dto.getSenderId());
        msg.setReceiverId(dto.getReceiverId());
        msg.setContent(dto.getContent());
        msg.setCreatedAt(LocalDateTime.now());
        
        ChatMessage savedMsg = chatService.save(msg);

        // 2. 🔥 CHECK IF RECEIVER IS META AI (ID 9999)
        if (dto.getReceiverId() == 9999L) {
            
            // Run AI Logic in a separate thread to avoid blocking the user
            new Thread(() -> {
                try {
                    // Call Gemini API
                    String aiReplyText = geminiService.getGeminiResponse(dto.getContent());

                    // Create AI Response Message
                    ChatMessage aiMsg = new ChatMessage();
                    aiMsg.setSenderId(9999L); // Bot ID
                    aiMsg.setReceiverId(dto.getSenderId()); // Reply to User
                    aiMsg.setContent(aiReplyText);
                    aiMsg.setCreatedAt(LocalDateTime.now());

                    // Save to DB
                    ChatMessage savedAiMsg = chatService.save(aiMsg);

                    // 🚀 PUSH TO WEBSOCKET (So user sees it instantly)
                    messagingTemplate.convertAndSend(
                        "/topic/messages/" + dto.getSenderId(), 
                        savedAiMsg
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        return ResponseEntity.ok(savedMsg);
    }

    // ✅ CHAT HISTORY
    
 // Inside ChatController.java

    @GetMapping("/history/{me}/{other}")
    public ResponseEntity<?> history(
            @PathVariable Long me,
            @PathVariable Long other,
            @RequestParam(required = false) Long groupId // ✅ Add this
    ) {
        // Pass 'other' as receiver and the new groupId parameter
        return ResponseEntity.ok(chatService.getHistory(me, other, groupId));
    }

    // ✅ CHAT LIST
    @GetMapping("/chats/{myId}")
    public ResponseEntity<List<ChatListDTO>> chats(@PathVariable Long myId) {
        return ResponseEntity.ok(chatService.getChats(myId));
    }
}