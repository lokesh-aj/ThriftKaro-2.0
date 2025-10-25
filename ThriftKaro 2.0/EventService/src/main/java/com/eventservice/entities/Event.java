package com.eventservice.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Document(collection = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    
    @Id
    private String id;
    
    private String name;
    private String description;
    private String category;
    private Date start_Date;
    private Date Finish_Date;
    private String status = "Running";
    private String tags;
    private Double originalPrice;
    private Double discountPrice;
    private Integer stock;
    private List<ImageData> images;
    private String shopId;
    private Map<String, Object> shop;
    private Integer sold_out = 0;
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageData {
        private String public_id;
        private String url;
    }
}


