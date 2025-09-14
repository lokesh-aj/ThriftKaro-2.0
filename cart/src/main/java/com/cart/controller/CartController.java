package com.cart.controller;

import com.cart.dto.AddItemRequest;
import com.cart.dto.CartResponse;
import com.cart.security.JwtUtil;
import com.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/carts")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/{userId}")
    public ResponseEntity<CartResponse> createCart(@PathVariable Long userId, 
                                                   Authentication authentication) {
        try {
            // Validate that the authenticated user matches the requested userId
            String token = getTokenFromAuthentication(authentication);
            Long authenticatedUserId = jwtUtil.extractUserId(token);
            
            if (!authenticatedUserId.equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            CartResponse cart = cartService.createCart(userId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartResponse> addItemToCart(@PathVariable Long cartId,
                                                      @RequestBody AddItemRequest request,
                                                      Authentication authentication) {
        try {
            String token = getTokenFromAuthentication(authentication);
            Long userId = jwtUtil.extractUserId(token);
            
            // Debug: Log the extracted values
            System.out.println("Extracted userId: " + userId);
            System.out.println("Requested cartId: " + cartId);
            
            // First, ensure the user has a cart
            CartResponse userCart;
            try {
                userCart = cartService.getCartByUserId(userId);
                System.out.println("Found existing cart: " + userCart.getCartId());
            } catch (RuntimeException e) {
                // If cart doesn't exist for user, create it first
                if (e.getMessage().contains("not found")) {
                    System.out.println("Cart not found for user: " + userId + ", creating new cart");
                    userCart = cartService.createCart(userId);
                    System.out.println("Created new cart: " + userCart.getCartId());
                } else {
                    System.out.println("Error getting cart: " + e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }
            
            // Validate that the requested cartId matches the user's cart
            if (!userCart.getCartId().equals(cartId)) {
                System.out.println("Cart ownership validation failed. User's cart: " + userCart.getCartId() + ", requested cart: " + cartId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            CartResponse updatedCart = cartService.addItemToCart(cartId, request.getProductId(), 
                                                                request.getQuantity(), token);
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException e) {
            System.out.println("Error in addItemToCart: " + e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("Insufficient stock")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<CartResponse> removeItemFromCart(@PathVariable Long cartId,
                                                           @PathVariable Long productId,
                                                           Authentication authentication) {
        try {
            String token = getTokenFromAuthentication(authentication);
            Long userId = jwtUtil.extractUserId(token);
            
            // Validate cart ownership
            CartResponse cart = cartService.getCartByUserId(userId);
            if (!cart.getCartId().equals(cartId)) {
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
    public ResponseEntity<CartResponse> getCartByUserId(@PathVariable Long userId,
                                                        Authentication authentication) {
        try {
            String token = getTokenFromAuthentication(authentication);
            Long authenticatedUserId = jwtUtil.extractUserId(token);
            
            if (!authenticatedUserId.equals(userId)) {
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
    public ResponseEntity<Void> clearCart(@PathVariable Long cartId,
                                          Authentication authentication) {
        try {
            String token = getTokenFromAuthentication(authentication);
            Long userId = jwtUtil.extractUserId(token);
            
            // Validate cart ownership
            CartResponse cart = cartService.getCartByUserId(userId);
            if (!cart.getCartId().equals(cartId)) {
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
                Long userId = jwtUtil.extractUserId(token);
                boolean isValid = jwtUtil.validateToken(token);
                
                return ResponseEntity.ok(Map.of(
                    "email", email,
                    "userId", userId,
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
    public ResponseEntity<CartResponse> addItemToUserCart(@RequestBody AddItemRequest request,
                                                          Authentication authentication) {
        try {
            String token = getTokenFromAuthentication(authentication);
            Long userId = jwtUtil.extractUserId(token);
            
            System.out.println("Adding item to cart for user: " + userId);
            
            // First, ensure the user has a cart
            CartResponse userCart;
            try {
                userCart = cartService.getCartByUserId(userId);
                System.out.println("Found existing cart: " + userCart.getCartId());
            } catch (RuntimeException e) {
                // If cart doesn't exist for user, create it first
                if (e.getMessage().contains("not found")) {
                    System.out.println("Cart not found for user: " + userId + ", creating new cart");
                    userCart = cartService.createCart(userId);
                    System.out.println("Created new cart: " + userCart.getCartId());
                } else {
                    System.out.println("Error getting cart: " + e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }
            
            CartResponse updatedCart = cartService.addItemToCart(userCart.getCartId(), request.getProductId(), 
                                                                request.getQuantity(), token);
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException e) {
            System.out.println("Error in addItemToUserCart: " + e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("Insufficient stock")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private String getTokenFromAuthentication(Authentication authentication) {
        // This is a simplified approach. In a real implementation, you might want to
        // store the token in the SecurityContext or pass it through headers
        return (String) authentication.getCredentials();
    }
}


