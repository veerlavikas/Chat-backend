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
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired private ChatMessageRepository chatRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private GroupRepository groupRepo;

    public ChatMessage save(ChatMessage msg) {
        return chatRepo.save(msg);
    }

    // ✅ THE UPDATED GET CHATS METHOD
    public List<ChatListDTO> getChats(Long myId) {
        List<ChatListDTO> chatList = new ArrayList<>();
        Set<Long> processedIds = new HashSet<>();

        // 1. FETCH PRIVATE CHATS
        // (Logic: Get all messages involving me, group by other user)
        List<ChatMessage> allMyMessages = chatRepo.findBySenderIdOrReceiverId(myId, myId);
        
        // Sort by newest first to capture the latest message
        allMyMessages.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        for (ChatMessage msg : allMyMessages) {
            // Skip if this is a Group Message (we handle groups separately below)
            if (msg.getGroupId() != null) continue;

            Long otherUserId = msg.getSenderId().equals(myId) ? msg.getReceiverId() : msg.getSenderId();

            if (!processedIds.contains(otherUserId)) {
                User otherUser = userRepo.findById(otherUserId).orElse(null);
                if (otherUser != null) {
                    ChatListDTO dto = new ChatListDTO();
                    dto.setUserId(otherUser.getId());
                    dto.setUsername(otherUser.getUsername());
                    dto.setProfilePic(otherUser.getProfilePic());
                    dto.setLastMessage(msg.getContent() != null ? msg.getContent() : "Sent an image");
                    dto.setLastMessageTime(msg.getCreatedAt());
                    
                    chatList.add(dto);
                    processedIds.add(otherUserId);
                }
            }
        }

        // 2. FETCH GROUP CHATS
        List<ChatGroup> myGroups = groupRepo.findMyGroups(myId);
        
        for (ChatGroup group : myGroups) {
            ChatListDTO groupDto = new ChatListDTO();
            groupDto.setGroupId(group.getId()); // ✅ Set Group ID
            groupDto.setUsername(group.getName()); // ✅ Set Group Name as Username
            groupDto.setProfilePic(group.getGroupIcon());
            
            // Find last message for this group
            ChatMessage lastGroupMsg = chatRepo.findTopByGroupIdOrderByCreatedAtDesc(group.getId());
            
            if (lastGroupMsg != null) {
                groupDto.setLastMessage(lastGroupMsg.getContent() != null ? lastGroupMsg.getContent() : "Image");
                groupDto.setLastMessageTime(lastGroupMsg.getCreatedAt());
            } else {
                groupDto.setLastMessage("Group created");
                groupDto.setLastMessageTime(java.time.LocalDateTime.now()); // Placeholder
            }
            
            chatList.add(groupDto);
        }

        // 3. SORT EVERYTHING BY TIME (Newest First)
        chatList.sort((a, b) -> {
            if (a.getLastMessageTime() == null) return 1;
            if (b.getLastMessageTime() == null) return -1;
            return b.getLastMessageTime().compareTo(a.getLastMessageTime());
        });

        return chatList;
    }
    
    // Helper for History (unchanged)
 // Inside ChatService.java

    public List<ChatMessage> getHistory(Long me, Long other, Long groupId) {
        // We now use the custom query method defined in the Repository
        return chatRepo.findChatHistory(me, other, groupId);
    }
}