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
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"}, allowCredentials = "true")
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
            
            // Handle numeric values accepting both numbers and numeric strings
            Object originalPriceObj = requestBody.get("originalPrice");
            if (originalPriceObj != null) {
                if (originalPriceObj instanceof Number n) {
                    event.setOriginalPrice(n.doubleValue());
                } else if (originalPriceObj instanceof String s && !s.isBlank()) {
                    event.setOriginalPrice(Double.parseDouble(s));
                }
            }
            Object discountPriceObj = requestBody.get("discountPrice");
            if (discountPriceObj != null) {
                if (discountPriceObj instanceof Number n) {
                    event.setDiscountPrice(n.doubleValue());
                } else if (discountPriceObj instanceof String s && !s.isBlank()) {
                    event.setDiscountPrice(Double.parseDouble(s));
                }
            }
            Object stockObj = requestBody.get("stock");
            if (stockObj != null) {
                if (stockObj instanceof Number n) {
                    event.setStock(n.intValue());
                } else if (stockObj instanceof String s && !s.isBlank()) {
                    event.setStock(Integer.parseInt(s));
                }
            }
            
            // Handle dates
            if (requestBody.get("start_Date") != null) {
                Object sd = requestBody.get("start_Date");
                if (sd instanceof Number n) {
                    event.setStart_Date(new java.util.Date(n.longValue()));
                } else if (sd instanceof String s && !s.isBlank()) {
                    try {
                        java.time.Instant inst = java.time.Instant.parse(s);
                        event.setStart_Date(java.util.Date.from(inst));
                    } catch (Exception ex) {
                        event.setStart_Date(new java.util.Date(java.time.LocalDate.parse(s).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()));
                    }
                }
            }
            if (requestBody.get("Finish_Date") != null) {
                Object fd = requestBody.get("Finish_Date");
                if (fd instanceof Number n) {
                    event.setFinish_Date(new java.util.Date(n.longValue()));
                } else if (fd instanceof String s && !s.isBlank()) {
                    try {
                        java.time.Instant inst = java.time.Instant.parse(s);
                        event.setFinish_Date(java.util.Date.from(inst));
                    } catch (Exception ex) {
                        event.setFinish_Date(new java.util.Date(java.time.LocalDate.parse(s).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()));
                    }
                }
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


