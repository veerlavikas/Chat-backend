package com.chat.backend.service;

import com.chat.backend.dto.ChatListDTO;
import com.chat.backend.entity.ChatGroup;
import com.chat.backend.entity.ChatMessage;
import com.chat.backend.entity.User;
import com.chat.backend.repository.ChatMessageRepository;
import com.chat.backend.repository.GroupRepository;
import com.chat.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatService {

    @Autowired private ChatMessageRepository chatRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private GroupRepository groupRepo;

    public ChatMessage save(ChatMessage msg) {
        return chatRepo.save(msg);
    }

    /**
     * ✅ THE UPDATED GET CHATS METHOD (Phone-based)
     */
    public List<ChatListDTO> getChats(String myPhone) {
        List<ChatListDTO> chatList = new ArrayList<>();
        Set<String> processedPhones = new HashSet<>();

        // 1. FETCH PRIVATE CHATS
        // Logic: Get all messages where I am sender OR receiver
        List<ChatMessage> allMyMessages = chatRepo.findBySenderPhoneOrReceiverPhone(myPhone, myPhone);
        
        // Sort by newest first
        allMyMessages.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        for (ChatMessage msg : allMyMessages) {
            if (msg.getGroupId() != null) continue;

            String otherPhone = msg.getSenderPhone().equals(myPhone) ? msg.getReceiverPhone() : msg.getSenderPhone();

            if (!processedPhones.contains(otherPhone)) {
                // Find the other user's profile info
                User otherUser = userRepo.findByPhone(otherPhone).orElse(null);
                
                if (otherUser != null || "9999".equals(otherPhone)) {
                    ChatListDTO dto = new ChatListDTO();
                    
                    if ("9999".equals(otherPhone)) {
                        dto.setUsername("Meta AI");
                        dto.setPhone("9999");
                        dto.setProfilePic("https://upload.wikimedia.org/wikipedia/commons/7/7b/Meta_Platforms_Inc._logo.svg"); // Placeholder Meta logo
                    } else {
                        dto.setUsername(otherUser.getUsername());
                        dto.setPhone(otherUser.getPhone());
                        dto.setProfilePic(otherUser.getProfilePic());
                    }

                    dto.setLastMessage(msg.getContent() != null ? msg.getContent() : "Sent a file");
                    dto.setLastMessageTime(msg.getCreatedAt());
                    
                    chatList.add(dto);
                    processedPhones.add(otherPhone);
                }
            }
        }

        // 2. FETCH GROUP CHATS
        // We still use Long for group identification as it's a relational ID
        List<ChatGroup> myGroups = groupRepo.findGroupsByMemberPhone(myPhone);
        
        for (ChatGroup group : myGroups) {
            ChatListDTO groupDto = new ChatListDTO();
            groupDto.setGroupId(group.getId());
            groupDto.setUsername(group.getName());
            groupDto.setProfilePic(group.getGroupIcon());
            
            ChatMessage lastGroupMsg = chatRepo.findTopByGroupIdOrderByCreatedAtDesc(group.getId());
            
            if (lastGroupMsg != null) {
                groupDto.setLastMessage(lastGroupMsg.getContent() != null ? lastGroupMsg.getContent() : "File");
                groupDto.setLastMessageTime(lastGroupMsg.getCreatedAt());
            } else {
                groupDto.setLastMessage("Group created");
                groupDto.setLastMessageTime(group.getCreatedAt());
            }
            
            chatList.add(groupDto);
        }

        // 3. SORT EVERYTHING BY TIME (WhatsApp style)
        chatList.sort((a, b) -> {
            if (a.getLastMessageTime() == null) return 1;
            if (b.getLastMessageTime() == null) return -1;
            return b.getLastMessageTime().compareTo(a.getLastMessageTime());
        });

        return chatList;
    }
    
    /**
     * ✅ FETCH CHAT HISTORY (Phone-based)
     */
    /**
     * ✅ FETCH CHAT HISTORY (Refined)
     */
    public List<ChatMessage> getHistory(String me, String other, Long groupId) {
        // If it's a group chat, we only care about the groupId
        if (groupId != null) {
            return chatRepo.findByGroupIdOrderByCreatedAtAsc(groupId);
        }
        
        // If it's a private chat, we need both phone numbers
        if (me != null && other != null) {
            return chatRepo.findChatHistory(me, other, null);
        }
        
        return new ArrayList<>();
    }
}