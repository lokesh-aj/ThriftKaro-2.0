package com.chatservice.controller;

import com.chatservice.dto.CreateMessageRequest;
import com.chatservice.entities.Message;
import com.chatservice.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/message")
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/create-new-message")
    public ResponseEntity<Map<String, Object>> createMessage(@RequestBody CreateMessageRequest request) {
        try {
            Message message = messageService.createMessage(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", message);
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            log.error("Error creating message: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/get-all-messages/{id}")
    public ResponseEntity<Map<String, Object>> getMessagesByConversationId(@PathVariable String id) {
        try {
            List<Message> messages = messageService.getMessagesByConversationId(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("messages", messages);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting messages for conversation {}: {}", id, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}

