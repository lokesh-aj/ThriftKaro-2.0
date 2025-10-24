package com.productservice.controller;

import com.productservice.entities.Product;
import com.productservice.security.JwtUtil;
import com.productservice.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
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
				return role != null ? role.toString() : "USER";
			}
		}
		return "USER";
	}

	// Helper method to check if user is seller
	private boolean isSeller(Authentication authentication) {
		return "SELLER".equals(getCurrentUserRole(authentication));
	}

	@PostMapping
	public ResponseEntity<?> addProduct(@RequestBody Product product, Authentication authentication) {
		if (!isSeller(authentication)) {
			return ResponseEntity.status(403).body("Access denied. Only sellers can add products.");
		}
		
		String userEmail = getCurrentUserEmail(authentication);
		System.out.println("Seller " + userEmail + " is adding a new product: " + product.getName());
		Product created = productService.addProduct(product);
		return ResponseEntity.created(URI.create("/products/" + created.getId())).body(created);
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

	@GetMapping("/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable String id) {
		System.out.println("Public request to view product with ID: " + id);
		Product product = productService.getProductById(id);
		if (product == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(product);
	}

	@GetMapping("/api/v2/product/seller/{shopId}")
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
		
		return ResponseEntity.ok("Product deleted successfully");
	}
} 