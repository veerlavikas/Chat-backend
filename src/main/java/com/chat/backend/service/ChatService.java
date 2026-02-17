package com.chat.backend.service;

import com.chat.backend.dto.ChatListDTO;
import com.chat.backend.entity.ChatMessage;
import com.chat.backend.entity.User;
import com.chat.backend.repository.ChatMessageRepository;
import com.chat.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository chatRepo;
    private final UserRepository userRepo;

    public ChatService(ChatMessageRepository chatRepo,
                       UserRepository userRepo) {
        this.chatRepo = chatRepo;
        this.userRepo = userRepo;
    }

    // ✅ Chat history
    public List<ChatMessage> getHistory(Long me, Long other) {
        return chatRepo.findChatHistory(me, other);
    }

    // ✅ Chat list
    public List<ChatListDTO> getChats(Long myId) {
        List<ChatMessage> messages = chatRepo.findChatList(myId);
        List<ChatListDTO> result = new ArrayList<>();

        for (ChatMessage m : messages) {
            Long otherId =
                m.getSenderId().equals(myId)
                    ? m.getReceiverId()
                    : m.getSenderId();

            User user = userRepo.findById(otherId).orElse(null);
            if (user == null) continue;

            result.add(new ChatListDTO(
                user.getId(),
                user.getUsername(),
                user.getProfilePic(),
                m.getContent() != null ? m.getContent() : "Media",
                m.getCreatedAt()
            ));
        }
        return result;
    }

    // ✅ Save message
    public ChatMessage save(ChatMessage msg) {
        return chatRepo.save(msg);
    }
}
