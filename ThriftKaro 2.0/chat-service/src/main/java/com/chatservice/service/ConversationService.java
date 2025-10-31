package com.chatservice.service;

import com.chatservice.dto.CreateConversationRequest;
import com.chatservice.dto.UpdateLastMessageRequest;
import com.chatservice.entities.Conversation;
import com.chatservice.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class ConversationService {

    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    private final ConversationRepository conversationRepository;
    
    public Conversation createConversation(CreateConversationRequest request) {
        // Check if conversation already exists
        Conversation existingConversation = conversationRepository.findByGroupTitle(request.getGroupTitle());
        
        if (existingConversation != null) {
            return existingConversation;
        }
        
        Conversation conversation = Conversation.builder()
                .groupTitle(request.getGroupTitle())
                .members(Arrays.asList(request.getUserId(), request.getSellerId()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return conversationRepository.save(conversation);
    }
    
    public List<Conversation> getConversationsBySellerId(String sellerId) {
        return conversationRepository.findByMembersContainingOrderByUpdatedAtDescCreatedAtDesc(sellerId);
    }
    
    public List<Conversation> getConversationsByUserId(String userId) {
        return conversationRepository.findByMembersContainingOrderByUpdatedAtDescCreatedAtDesc(userId);
    }
    
    public Conversation updateLastMessage(String conversationId, UpdateLastMessageRequest request) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        conversation.setLastMessage(request.getLastMessage());
        conversation.setLastMessageId(request.getLastMessageId());
        conversation.setUpdatedAt(LocalDateTime.now());
        
        return conversationRepository.save(conversation);
    }
}

