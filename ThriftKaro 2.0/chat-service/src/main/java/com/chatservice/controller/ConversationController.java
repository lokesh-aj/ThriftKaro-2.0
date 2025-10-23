package com.chatservice.controller;

import com.chatservice.dto.CreateConversationRequest;
import com.chatservice.dto.UpdateLastMessageRequest;
import com.chatservice.entities.Conversation;
import com.chatservice.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/conversation")
@RequiredArgsConstructor
@Slf4j
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping("/create-new-conversation")
    public ResponseEntity<Map<String, Object>> createConversation(@RequestBody CreateConversationRequest request) {
        try {
            Conversation conversation = conversationService.createConversation(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("conversation", conversation);
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            log.error("Error creating conversation: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/get-all-conversation-seller/{id}")
    public ResponseEntity<Map<String, Object>> getConversationsBySellerId(
            @PathVariable String id,
            @RequestHeader("Authorization") String token) {
        try {
            List<Conversation> conversations = conversationService.getConversationsBySellerId(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("conversations", conversations);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting conversations for seller {}: {}", id, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/get-all-conversation-user/{id}")
    public ResponseEntity<Map<String, Object>> getConversationsByUserId(
            @PathVariable String id,
            @RequestHeader("Authorization") String token) {
        try {
            List<Conversation> conversations = conversationService.getConversationsByUserId(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("conversations", conversations);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting conversations for user {}: {}", id, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/update-last-message/{id}")
    public ResponseEntity<Map<String, Object>> updateLastMessage(
            @PathVariable String id,
            @RequestBody UpdateLastMessageRequest request) {
        try {
            Conversation conversation = conversationService.updateLastMessage(id, request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("conversation", conversation);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating last message: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}

