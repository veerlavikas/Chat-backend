package com.chat.backend.controller;

import com.chat.backend.entity.ChatMessage;
import com.chat.backend.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final ChatService service;
    private final SimpMessagingTemplate template;

    public WebSocketController(ChatService service,
                               SimpMessagingTemplate template) {
        this.service = service;
        this.template = template;
    }

    /**
     * 🔥 Send message (TEXT / IMAGE / AUDIO)
     * Destination: /app/chat.send
     */
    @MessageMapping("/chat.send")
    public void send(ChatMessage msg) {

        // 1️⃣ Save message to DB
        ChatMessage saved = service.save(msg);

        // 2️⃣ Send to receiver (real-time)
        template.convertAndSend(
                "/topic/chat/" + msg.getReceiverId(),
                saved
        );

        // 3️⃣ Send back to sender (sync UI)
        template.convertAndSend(
                "/topic/chat/" + msg.getSenderId(),
                saved
        );
    }

    /**
     * ⌨️ Typing indicator
     * Destination: /app/chat.typing
     */
    @MessageMapping("/chat.typing")
    public void typing(ChatMessage msg) {
        template.convertAndSend(
                "/topic/typing/" + msg.getReceiverId(),
                msg
        );
    }

    /**
     * 👀 Seen indicator
     * Destination: /app/chat.seen
     */
    @MessageMapping("/chat.seen")
    public void seen(ChatMessage msg) {
        template.convertAndSend(
                "/topic/seen/" + msg.getSenderId(),
                msg
        );
    }
}
