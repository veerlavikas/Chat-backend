package com.chat.backend.controller;

import com.chat.backend.dto.ChatListDTO;
import com.chat.backend.dto.MessageDTO;
import com.chat.backend.entity.ChatMessage;
import com.chat.backend.service.ChatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // ✅ SEND MESSAGE
    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody MessageDTO dto) {

        ChatMessage msg = new ChatMessage();
        msg.setSenderId(dto.getSenderId());
        msg.setReceiverId(dto.getReceiverId());
        msg.setContent(dto.getContent());

        return ResponseEntity.ok(chatService.save(msg));
    }

    // ✅ CHAT HISTORY
    @GetMapping("/history/{me}/{other}")
    public ResponseEntity<?> history(
            @PathVariable Long me,
            @PathVariable Long other
    ) {
        return ResponseEntity.ok(chatService.getHistory(me, other));
    }

    // 🔥🔥🔥 THIS WAS MISSING 🔥🔥🔥
    // ✅ WHATSAPP-STYLE CHAT LIST
    @GetMapping("/chats/{myId}")
    public ResponseEntity<List<ChatListDTO>> chats(
            @PathVariable Long myId
    ) {
        return ResponseEntity.ok(chatService.getChats(myId));
    }
}
