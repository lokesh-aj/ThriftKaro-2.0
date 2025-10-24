package com.productservice.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
	@Id
	private String id;

	private String name;

	private String description;

	private String category;

	private String tags;

	private BigDecimal originalPrice;

	private BigDecimal discountPrice;

	private Integer stock;

	private List<Map<String, Object>> images;

	private List<Map<String, Object>> reviews;

	private BigDecimal ratings;

	private String shopId;

	private Map<String, Object> shop;

	private Integer soldOut = 0;

	private LocalDateTime createdAt = LocalDateTime.now();
} 