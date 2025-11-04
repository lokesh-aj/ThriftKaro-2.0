package com.cart.controller;

import com.cart.dto.AddItemRequest;
import com.cart.dto.CartResponse;
import com.cart.security.JwtUtil;
import com.cart.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v2/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/{userId}")
    public ResponseEntity<CartResponse> createCart(@PathVariable String userId, 
                                                   HttpServletRequest request) {
        try {
            // Extract token from Authorization header
            String token = getTokenFromRequest(request);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Validate that the authenticated user matches the requested userId
            String authenticatedUserId = jwtUtil.extractUserId(token);
            
            if (authenticatedUserId == null) {
                System.err.println("WARNING: Token does not contain userId claim! User needs to log in again.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Normal validation: userId from token must match requested userId
            if (!authenticatedUserId.equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            CartResponse cart = cartService.createCart(userId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            System.out.println("Error in createCart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartResponse> addItemToCart(@PathVariable String cartId,
                                                      @RequestBody AddItemRequest request,
                                                      HttpServletRequest httpRequest) {
        try {
            String token = getTokenFromRequest(httpRequest);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            String userId = jwtUtil.extractUserId(token);
            
            // Validate request
            if (request.getProductId() == null || request.getProductId().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            if (request.getQuantity() == null || request.getQuantity() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // First, ensure the user has a cart
            CartResponse userCart;
            try {
                userCart = cartService.getCartByUserId(userId);
            } catch (RuntimeException e) {
                // If cart doesn't exist for user, create it first
                if (e.getMessage().contains("not found")) {
                    userCart = cartService.createCart(userId);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }
            
            // Validate that the requested cartId matches the user's cart
            if (!userCart.getCartId().equals(cartId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            CartResponse updatedCart = cartService.addItemToCart(cartId, request.getProductId(), 
                                                                request.getQuantity(), token);
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException e) {
            System.err.println("ERROR in addItemToCart: " + e.getMessage());
            e.printStackTrace();
            if (e.getMessage() != null) {
                if (e.getMessage().contains("not found")) {
                    return ResponseEntity.notFound().build();
                } else if (e.getMessage().contains("Insufficient stock")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                } else if (e.getMessage().contains("Failed to fetch product")) {
                    System.err.println("Product Service communication error: " + e.getMessage());
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
                }
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            System.err.println("UNEXPECTED ERROR in addItemToCart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<CartResponse> removeItemFromCart(@PathVariable String cartId,
                                                           @PathVariable String productId,
                                                           HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            String userId = jwtUtil.extractUserId(token);
            System.out.println("Extracted userId from token: " + userId);
            System.out.println("Requested cartId: " + cartId);
            
            if (userId == null) {
                System.out.println("ERROR: Could not extract userId from token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Validate cart ownership
            CartResponse cart = cartService.getCartByUserId(userId);
            if (!cart.getCartId().equals(cartId)) {
                System.out.println("ERROR: Cart ownership mismatch. User's cart: " + cart.getCartId() + ", Requested cart: " + cartId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            CartResponse updatedCart = cartService.removeItemFromCart(cartId, productId);
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCartByUserId(@PathVariable String userId,
                                                        HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            String authenticatedUserId = jwtUtil.extractUserId(token);
            System.out.println("Extracted userId from token: " + authenticatedUserId);
            System.out.println("Requested userId: " + userId);
            
            if (authenticatedUserId == null) {
                System.out.println("ERROR: Could not extract userId from token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            if (!authenticatedUserId.equals(userId)) {
                System.out.println("ERROR: UserId mismatch. Token userId: " + authenticatedUserId + ", Request userId: " + userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            CartResponse cart = cartService.getCartByUserId(userId);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{cartId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable String cartId,
                                          HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            String userId = jwtUtil.extractUserId(token);
            System.out.println("Extracted userId from token: " + userId);
            System.out.println("Requested cartId: " + cartId);
            
            if (userId == null) {
                System.out.println("ERROR: Could not extract userId from token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Validate cart ownership
            CartResponse cart = cartService.getCartByUserId(userId);
            if (!cart.getCartId().equals(cartId)) {
                System.out.println("ERROR: Cart ownership mismatch. User's cart: " + cart.getCartId() + ", Requested cart: " + cartId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            cartService.clearCart(cartId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/test-jwt")
    public ResponseEntity<?> testJwt(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String email = jwtUtil.extractEmail(token);
                String userId = jwtUtil.extractUserId(token);
                boolean isValid = jwtUtil.validateToken(token);
                
                return ResponseEntity.ok(Map.of(
                    "email", email,
                    "userId", userId != null ? userId : "null",
                    "isValid", isValid,
                    "tokenPreview", token.substring(0, Math.min(20, token.length())) + "..."
                ));
            } else {
                return ResponseEntity.badRequest().body("No valid Authorization header");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/items")
    public ResponseEntity<?> addItemToUserCart(@RequestBody AddItemRequest request,
                                                          HttpServletRequest httpRequest) {
        try {
            // Validate request body
            if (request == null) {
                System.err.println("ERROR: Request body is null");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Request body is required", "message", "Request body cannot be null"));
            }
            
            // Validate productId
            if (request.getProductId() == null || request.getProductId().isEmpty()) {
                System.err.println("ERROR: ProductId is null or empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Product ID is required", "message", "ProductId cannot be null or empty"));
            }
            
            // Validate quantity
            if (request.getQuantity() == null || request.getQuantity() <= 0) {
                System.err.println("ERROR: Quantity is null or invalid: " + request.getQuantity());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid quantity", "message", "Quantity must be greater than 0"));
            }
            
            String token = getTokenFromRequest(httpRequest);
            if (token == null) {
                System.err.println("ERROR: No token found in request");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication required", "message", "No token found in request"));
            }
            
            String userId = jwtUtil.extractUserId(token);
            if (userId == null) {
                System.err.println("ERROR: Could not extract userId from token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token", "message", "Could not extract userId from token"));
            }
            
            System.out.println("addItemToUserCart - userId: " + userId + ", productId: " + request.getProductId() + ", quantity: " + request.getQuantity());
            
            // First, ensure the user has a cart
            CartResponse userCart;
            try {
                userCart = cartService.getCartByUserId(userId);
            } catch (RuntimeException e) {
                // If cart doesn't exist for user, create it first
                if (e.getMessage() != null && e.getMessage().contains("not found")) {
                    System.out.println("Cart not found for user " + userId + ", creating new cart...");
                    userCart = cartService.createCart(userId);
                } else {
                    System.err.println("ERROR getting cart for user " + userId + ": " + e.getMessage());
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "Failed to get or create cart", "message", e.getMessage()));
                }
            }
            
            CartResponse updatedCart = cartService.addItemToCart(userCart.getCartId(), request.getProductId(), 
                                                                request.getQuantity(), token);
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException e) {
            System.err.println("ERROR in addItemToUserCart (RuntimeException): " + e.getMessage());
            e.printStackTrace();
            if (e.getMessage() != null) {
                if (e.getMessage().contains("not found")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "Cart or product not found", "message", e.getMessage()));
                } else if (e.getMessage().contains("Insufficient stock")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("error", "Insufficient stock", "message", e.getMessage()));
                }
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error", "message", 
                        e.getMessage() != null ? e.getMessage() : "An unexpected error occurred"));
        } catch (Exception e) {
            System.err.println("ERROR in addItemToUserCart (Exception): " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error", "message", 
                        e.getMessage() != null ? e.getMessage() : "An unexpected error occurred"));
        }
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        // Extract token from Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}


