package com.productservice.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {
    private String name;
    private String description;
    private String category;
    private String tags;
    private BigDecimal originalPrice;
    private BigDecimal discountPrice;
    private Integer stock;
    private String shopId;
    
    // Accept images as JsonNode array - Jackson can deserialize this flexibly
    // JsonNode can represent strings, objects, or any JSON value
    private List<JsonNode> images;
}

