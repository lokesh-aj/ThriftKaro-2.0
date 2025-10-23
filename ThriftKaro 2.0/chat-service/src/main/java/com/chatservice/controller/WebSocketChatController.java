package com.chatservice.controller;

import com.chatservice.dto.ChatMessage;
import com.chatservice.dto.CreateMessageRequest;
import com.chatservice.entities.Message;
import com.chatservice.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketChatController {

    private final MessageService messageService;

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessage send(@Payload ChatMessage chatMessage) {
        log.info("Received message: {}", chatMessage);
        
        try {
            // Save message to database
            CreateMessageRequest createRequest = CreateMessageRequest.builder()
                    .conversationId(chatMessage.getConversationId())
                    .text(chatMessage.getText())
                    .sender(chatMessage.getSender())
                    .images(chatMessage.getImages())
                    .build();
            
            Message savedMessage = messageService.createMessage(createRequest);
            
            // Update the chat message with saved data
            chatMessage.setId(savedMessage.getId());
            chatMessage.setCreatedAt(savedMessage.getCreatedAt());
            chatMessage.setUpdatedAt(savedMessage.getUpdatedAt());
            
            log.info("Message saved successfully with ID: {}", savedMessage.getId());
            return chatMessage;
            
        } catch (Exception e) {
            log.error("Error saving message: {}", e.getMessage());
            // Return the message even if saving fails to maintain real-time communication
            chatMessage.setCreatedAt(LocalDateTime.now());
            chatMessage.setUpdatedAt(LocalDateTime.now());
            return chatMessage;
        }
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("conversationId", chatMessage.getConversationId());
        
        log.info("User {} joined conversation {}", chatMessage.getSender(), chatMessage.getConversationId());
        
        chatMessage.setType("JOIN");
        chatMessage.setText(chatMessage.getSender() + " joined the chat!");
        chatMessage.setCreatedAt(LocalDateTime.now());
        
        return chatMessage;
    }

    @MessageMapping("/chat.leave")
    @SendTo("/topic/public")
    public ChatMessage leave(@Payload ChatMessage chatMessage) {
        log.info("User {} left conversation {}", chatMessage.getSender(), chatMessage.getConversationId());
        
        chatMessage.setType("LEAVE");
        chatMessage.setText(chatMessage.getSender() + " left the chat!");
        chatMessage.setCreatedAt(LocalDateTime.now());
        
        return chatMessage;
    }
}
