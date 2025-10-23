package com.chatservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "messages")
@Data
@AllArgsConstructor
@Builder
public class Message {
    
    @Id
    private String id;
    
    private String conversationId;
    private String text;
    private String sender;
    private Map<String, String> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Message() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}

