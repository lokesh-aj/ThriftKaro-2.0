package com.chatservice.service;

import com.chatservice.dto.CreateMessageRequest;
import com.chatservice.entities.Message;
import com.chatservice.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    
    private final MessageRepository messageRepository;
    
    public Message createMessage(CreateMessageRequest request) {
        Map<String, String> images = null;
        
        // Handle image upload (this would typically integrate with Cloudinary)
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            // In a real implementation, you would upload to Cloudinary here
            // For now, we'll just create a mock response
            images = new HashMap<>();
            images.put("public_id", "mock_public_id");
            images.put("url", "mock_url");
        }
        
        Message message = Message.builder()
                .conversationId(request.getConversationId())
                .text(request.getText())
                .sender(request.getSender())
                .images(images)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return messageRepository.save(message);
    }
    
    public List<Message> getMessagesByConversationId(String conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }
}

