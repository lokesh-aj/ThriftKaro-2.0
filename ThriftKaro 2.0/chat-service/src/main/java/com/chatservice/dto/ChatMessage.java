package com.chatservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    private String id;
    private String conversationId;
    private String text;
    private String sender;
    private Map<String, String> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String type; // "CHAT", "JOIN", "LEAVE"
}
