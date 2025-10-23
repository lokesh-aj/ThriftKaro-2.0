package com.chatservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "conversations")
@Data
@AllArgsConstructor
@Builder
public class Conversation {
    
    @Id
    private String id;
    
    private String groupTitle;
    private List<String> members;
    private String lastMessage;
    private String lastMessageId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Conversation() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}

