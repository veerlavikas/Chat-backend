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

    public WebSocketController(ChatService service, SimpMessagingTemplate template) {
        this.service = service;
        this.template = template;
    }

    /**
     * 🔥 Send message (TEXT / IMAGE / AUDIO)
     * Destination from Mobile: /app/chat.send
     */
    @MessageMapping("/chat.send")
    public void send(ChatMessage msg) {
        // 1️⃣ Save message to DB
        // (This now uses the senderPhone and receiverPhone fields we added)
        ChatMessage saved = service.save(msg);

        // 2️⃣ Send to receiver (real-time private queue)
        // Subscribers on Mobile: /user/{receiverPhone}/queue/messages
        template.convertAndSendToUser(
                msg.getReceiverPhone(),
                "/queue/messages",
                saved
        );

        // 3️⃣ Send back to sender (To sync multiple devices if needed)
        // Subscribers on Mobile: /user/{senderPhone}/queue/messages
        template.convertAndSendToUser(
                msg.getSenderPhone(),
                "/queue/messages",
                saved
        );
    }

    /**
     * ⌨️ Typing indicator
     * Destination from Mobile: /app/chat.typing
     */
    @MessageMapping("/chat.typing")
    public void typing(ChatMessage msg) {
        // This notifies the receiver that 'senderPhone' is typing
        template.convertAndSendToUser(
                msg.getReceiverPhone(),
                "/queue/typing",
                msg
        );
    }

    /**
     * 👀 Seen indicator (Double Tick logic)
     * Destination from Mobile: /app/chat.seen
     */
    @MessageMapping("/chat.seen")
    public void seen(ChatMessage msg) {
        // Notify the original sender that their message was read
        template.convertAndSendToUser(
                msg.getSenderPhone(),
                "/queue/seen",
                msg
        );
    }
}