package com.productservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.productservice.dto.CreateProductRequest;
import com.productservice.entities.Product;
import com.productservice.security.JwtUtil;
import com.productservice.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/product")
public class ProductController {
	private final ProductService productService;
	private final JwtUtil jwtUtil;

	public ProductController(ProductService productService, JwtUtil jwtUtil) {
		this.productService = productService;
		this.jwtUtil = jwtUtil;
	}

	// Helper method to get current authenticated user
	private String getCurrentUserEmail(Authentication authentication) {
		return authentication != null ? authentication.getName() : "anonymous";
	}

	// Helper method to get current user role
	private String getCurrentUserRole(Authentication authentication) {
		if (authentication != null && authentication.getDetails() != null) {
			Object details = authentication.getDetails();
			if (details instanceof java.util.Map) {
				@SuppressWarnings("unchecked")
				java.util.Map<String, Object> detailsMap = (java.util.Map<String, Object>) details;
				Object role = detailsMap.get("role");
				if (role != null) {
					String roleStr = role.toString();
					System.out.println("Extracted role from authentication: " + roleStr);
					return roleStr;
				}
			}
		}
		System.out.println("Warning: No role found in authentication details. Defaulting to USER.");
		return "USER";
	}

	// Helper method to check if user is seller
	private boolean isSeller(Authentication authentication) {
		return "SELLER".equals(getCurrentUserRole(authentication));
	}

	@PostMapping
	public ResponseEntity<?> addProduct(@RequestBody Product product, Authentication authentication) {
		if (authentication == null) {
			System.out.println("ERROR: Authentication is null");
			return ResponseEntity.status(403).body("Access denied. Authentication required.");
		}
		
		String userRole = getCurrentUserRole(authentication);
		System.out.println("User attempting to add product - Role: " + userRole + ", Email: " + getCurrentUserEmail(authentication));
		
		if (!isSeller(authentication)) {
			System.out.println("ERROR: User is not a seller. Role: " + userRole);
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Access denied. Only sellers can add products.");
			errorResponse.put("userRole", userRole);
			errorResponse.put("requiredRole", "SELLER");
			return ResponseEntity.status(403).body(errorResponse);
		}
		
		String userEmail = getCurrentUserEmail(authentication);
		System.out.println("Seller " + userEmail + " is adding a new product: " + product.getName());
		Product created = productService.addProduct(product);
		
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("message", "Product created successfully!");
		response.put("product", created);
		
		return ResponseEntity.created(URI.create("/api/v2/product/" + created.getId())).body(response);
	}

	// Frontend compatibility endpoint for create-product
	@PostMapping("/create-product")
	public ResponseEntity<?> createProduct(@RequestBody CreateProductRequest request, Authentication authentication) {
		if (authentication == null) {
			System.out.println("ERROR: Authentication is null");
			return ResponseEntity.status(403).body("Access denied. Authentication required.");
		}
		
		String userRole = getCurrentUserRole(authentication);
		System.out.println("User attempting to add product - Role: " + userRole + ", Email: " + getCurrentUserEmail(authentication));
		
		if (!isSeller(authentication)) {
			System.out.println("ERROR: User is not a seller. Role: " + userRole);
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "Access denied. Only sellers can add products.");
			errorResponse.put("userRole", userRole);
			errorResponse.put("requiredRole", "SELLER");
			return ResponseEntity.status(403).body(errorResponse);
		}
		
		// Convert DTO to Product entity
		Product product = new Product();
		product.setName(request.getName());
		product.setDescription(request.getDescription());
		product.setCategory(request.getCategory());
		product.setTags(request.getTags());
		product.setOriginalPrice(request.getOriginalPrice());
		product.setDiscountPrice(request.getDiscountPrice());
		product.setStock(request.getStock());
		product.setShopId(request.getShopId());
		
		// Convert images from List<JsonNode> to List<Map<String, Object>>
		List<Map<String, Object>> imageMaps = new ArrayList<>();
		if (request.getImages() != null) {
			for (JsonNode imageNode : request.getImages()) {
				Map<String, Object> imageMap = new HashMap<>();
				if (imageNode.isTextual()) {
					// Base64 string from frontend
					String base64String = imageNode.asText();
					imageMap.put("public_id", "local");
					imageMap.put("url", base64String);
				} else if (imageNode.isObject()) {
					// Already an object, convert to Map
					imageMap.put("public_id", imageNode.has("public_id") ? imageNode.get("public_id").asText() : "local");
					imageMap.put("url", imageNode.has("url") ? imageNode.get("url").asText() : "");
				} else {
					// Handle any other type by converting to string
					imageMap.put("public_id", "local");
					imageMap.put("url", imageNode.asText());
				}
				imageMaps.add(imageMap);
			}
		}
		product.setImages(imageMaps);
		
		String userEmail = getCurrentUserEmail(authentication);
		System.out.println("Seller " + userEmail + " is adding a new product: " + product.getName());
		Product created = productService.addProduct(product);
		
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("message", "Product created successfully!");
		response.put("product", created);
		
		return ResponseEntity.created(URI.create("/api/v2/product/" + created.getId())).body(response);
	}

	// Health check endpoint (no authentication required)
	@GetMapping("/health")
	public ResponseEntity<String> healthCheck() {
		return ResponseEntity.ok("ProductService is running!");
	}

	// Debug endpoint to check authentication details
	@GetMapping("/debug")
	public ResponseEntity<?> debugAuth(Authentication authentication) {
		if (authentication == null) {
			return ResponseEntity.ok("No authentication found");
		}
		
		Map<String, Object> debugInfo = new HashMap<>();
		debugInfo.put("name", authentication.getName());
		debugInfo.put("authorities", authentication.getAuthorities());
		debugInfo.put("details", authentication.getDetails());
		debugInfo.put("principal", authentication.getPrincipal());
		debugInfo.put("isAuthenticated", authentication.isAuthenticated());
		
		// Extract role from details
		String role = getCurrentUserRole(authentication);
		debugInfo.put("extractedRole", role);
		debugInfo.put("isSeller", isSeller(authentication));
		
		return ResponseEntity.ok(debugInfo);
	}

	// Test JWT token validation directly
	@GetMapping("/test-jwt")
	public ResponseEntity<?> testJwt(@RequestHeader("Authorization") String authHeader) {
		Map<String, Object> result = new HashMap<>();
		
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			result.put("token", token.substring(0, Math.min(20, token.length())) + "...");
			
			try {
				boolean isValid = jwtUtil.validateToken(token);
				result.put("isValid", isValid);
				
				if (isValid) {
					String email = jwtUtil.extractEmail(token);
					String role = jwtUtil.extractRole(token);
					result.put("email", email);
					result.put("role", role);
				}
			} catch (Exception e) {
				result.put("error", e.getMessage());
			}
		} else {
			result.put("error", "No valid Authorization header");
		}
		
		return ResponseEntity.ok(result);
	}

	@GetMapping
	public ResponseEntity<List<Product>> getAllProducts() {
		System.out.println("Public request to view all products");
		return ResponseEntity.ok(productService.getAllProducts());
	}

	// Frontend compatibility endpoint for get-all-products
	@GetMapping("/get-all-products")
	public ResponseEntity<?> getAllProductsCompatibility() {
		System.out.println("Frontend request to view all products");
		List<Product> products = productService.getAllProducts();
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("products", products);
		return ResponseEntity.ok(response);
	}

	// Frontend compatibility endpoint for get-all-products-shop
	@GetMapping("/get-all-products-shop/{id}")
	public ResponseEntity<?> getAllProductsShop(@PathVariable String id) {
		System.out.println("Frontend request to get products for shop with ID: " + id);
		List<Product> products = productService.getProductsByShopId(id);
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("products", products);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable String id) {
		System.out.println("Public request to view product with ID: " + id);
		Product product = productService.getProductById(id);
		if (product == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(product);
	}

	@GetMapping("/seller/{shopId}")
	public ResponseEntity<List<Product>> getProductsBySeller(@PathVariable String shopId) {
		System.out.println("Request to get products for seller with shopId: " + shopId);
		List<Product> products = productService.getProductsByShopId(shopId);
		return ResponseEntity.ok(products);
	}

	@PutMapping("/{id}/stock")
	public ResponseEntity<?> updateStock(
			@PathVariable String id,
			@RequestParam("quantity") Integer quantity,
			Authentication authentication
	) {
		if (!isSeller(authentication)) {
			return ResponseEntity.status(403).body("Access denied. Only sellers can update stock.");
		}
		
		String userEmail = getCurrentUserEmail(authentication);
		System.out.println("Seller " + userEmail + " is updating stock for product ID: " + id + " to quantity: " + quantity);
		Product updated = productService.updateStock(id, quantity);
		if (updated == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteProduct(@PathVariable String id, Authentication authentication) {
		if (!isSeller(authentication)) {
			return ResponseEntity.status(403).body("Access denied. Only sellers can delete products.");
		}
		
		String userEmail = getCurrentUserEmail(authentication);
		System.out.println("Seller " + userEmail + " is deleting product with ID: " + id);
		
		boolean deleted = productService.deleteProduct(id);
		if (!deleted) {
			return ResponseEntity.notFound().build();
		}
		
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("message", "Product deleted successfully");
		return ResponseEntity.ok(response);
	}

	// Frontend compatibility endpoint for delete-shop-product
	@DeleteMapping("/delete-shop-product/{id}")
	public ResponseEntity<?> deleteShopProduct(@PathVariable String id, Authentication authentication) {
		return deleteProduct(id, authentication);
	}
} 