package com.eventservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.eventservice.entities.Event;
import com.eventservice.entities.Event.ImageData;
import com.eventservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventService {
    
    private final EventRepository eventRepository;
    private final Cloudinary cloudinary;
    
    public Event createEvent(Event event, List<String> imageBase64List) throws IOException {
        List<ImageData> imagesLinks = new ArrayList<>();
        
        // Upload images to Cloudinary
        if (imageBase64List != null && !imageBase64List.isEmpty()) {
            for (String imageBase64 : imageBase64List) {
                Map uploadResult = cloudinary.uploader().upload(imageBase64, 
                    ObjectUtils.asMap("folder", "events"));
                
                ImageData imageData = new ImageData();
                imageData.setPublic_id((String) uploadResult.get("public_id"));
                imageData.setUrl((String) uploadResult.get("secure_url"));
                imagesLinks.add(imageData);
            }
        }
        
        event.setImages(imagesLinks);
        return eventRepository.save(event);
    }
    
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    
    public List<Event> getEventsByShopId(String shopId) {
        return eventRepository.findByShopId(shopId);
    }
    
    public Event getEventById(String id) {
        return eventRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
    }
    
    public void deleteEvent(String id) throws IOException {
        Event event = getEventById(id);
        
        // Delete images from Cloudinary
        if (event.getImages() != null) {
            for (ImageData image : event.getImages()) {
                try {
                    cloudinary.uploader().destroy(image.getPublic_id(), ObjectUtils.emptyMap());
                } catch (Exception e) {
                    // Log error but continue with deletion
                    System.err.println("Failed to delete image from Cloudinary: " + image.getPublic_id());
                }
            }
        }
        
        eventRepository.deleteById(id);
    }
    
    public Event updateEvent(String id, Event eventUpdate) {
        Event event = getEventById(id);
        
        if (eventUpdate.getName() != null) event.setName(eventUpdate.getName());
        if (eventUpdate.getDescription() != null) event.setDescription(eventUpdate.getDescription());
        if (eventUpdate.getCategory() != null) event.setCategory(eventUpdate.getCategory());
        if (eventUpdate.getStart_Date() != null) event.setStart_Date(eventUpdate.getStart_Date());
        if (eventUpdate.getFinish_Date() != null) event.setFinish_Date(eventUpdate.getFinish_Date());
        if (eventUpdate.getStatus() != null) event.setStatus(eventUpdate.getStatus());
        if (eventUpdate.getTags() != null) event.setTags(eventUpdate.getTags());
        if (eventUpdate.getOriginalPrice() != null) event.setOriginalPrice(eventUpdate.getOriginalPrice());
        if (eventUpdate.getDiscountPrice() != null) event.setDiscountPrice(eventUpdate.getDiscountPrice());
        if (eventUpdate.getStock() != null) event.setStock(eventUpdate.getStock());
        if (eventUpdate.getSold_out() != null) event.setSold_out(eventUpdate.getSold_out());
        
        return eventRepository.save(event);
    }
}


