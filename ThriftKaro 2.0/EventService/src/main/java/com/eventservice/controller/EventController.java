package com.eventservice.controller;

import com.eventservice.entities.Event;
import com.eventservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/event")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class EventController {
    
    private final EventService eventService;
    
    @PostMapping("/create-event")
    public ResponseEntity<Map<String, Object>> createEvent(@RequestBody Map<String, Object> requestBody) {
        try {
            // Extract event data
            Event event = new Event();
            event.setName((String) requestBody.get("name"));
            event.setDescription((String) requestBody.get("description"));
            event.setCategory((String) requestBody.get("category"));
            event.setTags((String) requestBody.get("tags"));
            event.setShopId((String) requestBody.get("shopId"));
            event.setShop((Map<String, Object>) requestBody.get("shop"));
            
            // Handle numeric values
            if (requestBody.get("originalPrice") != null) {
                event.setOriginalPrice(((Number) requestBody.get("originalPrice")).doubleValue());
            }
            if (requestBody.get("discountPrice") != null) {
                event.setDiscountPrice(((Number) requestBody.get("discountPrice")).doubleValue());
            }
            if (requestBody.get("stock") != null) {
                event.setStock(((Number) requestBody.get("stock")).intValue());
            }
            
            // Handle dates
            if (requestBody.get("start_Date") != null) {
                event.setStart_Date(new java.util.Date((Long) requestBody.get("start_Date")));
            }
            if (requestBody.get("Finish_Date") != null) {
                event.setFinish_Date(new java.util.Date((Long) requestBody.get("Finish_Date")));
            }
            
            // Extract images
            List<String> images = null;
            Object imagesObj = requestBody.get("images");
            if (imagesObj instanceof List) {
                images = (List<String>) imagesObj;
            } else if (imagesObj instanceof String) {
                images = List.of((String) imagesObj);
            }
            
            Event createdEvent = eventService.createEvent(event, images);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("event", createdEvent);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    @GetMapping("/get-all-events")
    public ResponseEntity<Map<String, Object>> getAllEvents() {
        try {
            List<Event> events = eventService.getAllEvents();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("events", events);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    @GetMapping("/get-all-events/{shopId}")
    public ResponseEntity<Map<String, Object>> getEventsByShop(@PathVariable String shopId) {
        try {
            List<Event> events = eventService.getEventsByShopId(shopId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("events", events);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    @DeleteMapping("/delete-shop-event/{id}")
    public ResponseEntity<Map<String, Object>> deleteEvent(@PathVariable String id) {
        try {
            eventService.deleteEvent(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Event deleted successfully!");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    @GetMapping("/admin-all-events")
    public ResponseEntity<Map<String, Object>> getAllEventsForAdmin() {
        try {
            List<Event> events = eventService.getAllEvents();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("events", events);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @PutMapping("/update-event/{id}")
    public ResponseEntity<Map<String, Object>> updateEvent(
            @PathVariable String id,
            @RequestBody Event eventUpdate) {
        try {
            Event updatedEvent = eventService.updateEvent(id, eventUpdate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("event", updatedEvent);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}


