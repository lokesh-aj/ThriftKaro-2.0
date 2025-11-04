package com.cart.service;

import com.cart.dto.*;
import com.cart.entities.Cart;
import com.cart.entities.CartItem;
import com.cart.repository.CartItemRepository;
import com.cart.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ProductServiceClient productServiceClient;
    
    public CartResponse createCart(String userId) {
        // Check if cart already exists for user - use findFirst to handle duplicates
        Optional<Cart> existingCart = cartRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
            // If there are multiple carts, clean them up and use the most recent one
            List<Cart> allCarts = cartRepository.findAllByUserId(userId);
            if (allCarts.size() > 1) {
                System.out.println("Warning: Found " + allCarts.size() + " carts for user " + userId + ". Cleaning up duplicates...");
                // Merge items from all carts into the most recent one, then delete others
                for (int i = 1; i < allCarts.size(); i++) {
                    Cart duplicateCart = allCarts.get(i);
                    // Merge items from duplicate cart into main cart
                    List<CartItem> duplicateItems = cartItemRepository.findByCartId(duplicateCart.getCartId());
                    for (CartItem duplicateItem : duplicateItems) {
                        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getCartId(), duplicateItem.getProductId());
                        if (existingItem.isPresent()) {
                            // Update quantity if item already exists in main cart
                            CartItem existing = existingItem.get();
                            existing.setQuantity(existing.getQuantity() + duplicateItem.getQuantity());
                            cartItemRepository.save(existing);
                        } else {
                            // Create new item in main cart with same product and quantity
                            CartItem newItem = CartItem.builder()
                                    .cartId(cart.getCartId())
                                    .productId(duplicateItem.getProductId())
                                    .quantity(duplicateItem.getQuantity())
                                    .build();
                            cartItemRepository.save(newItem);
                        }
                    }
                    // Delete duplicate cart items and cart
                    cartItemRepository.deleteByCartId(duplicateCart.getCartId());
                    cartRepository.delete(duplicateCart);
                }
                System.out.println("Cleaned up duplicate carts. Using cart: " + cart.getCartId());
            }
            return convertToCartResponse(cart);
        }
        
        Cart cart = Cart.builder()
                .userId(userId)
                .build();
        
        Cart savedCart = cartRepository.save(cart);
        return convertToCartResponse(savedCart);
    }
    
    public CartResponse addItemToCart(String cartId, String productId, int quantity, String token) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + cartId));
        
        // Validate product exists and has sufficient stock
        ProductResponse product = null;
        try {
            product = productServiceClient.getProductById(productId, token);
        } catch (Exception e) {
            System.err.println("CartService.addItemToCart - Warning: Failed to validate product via ProductService: " + e.getMessage());
            // Continue without blocking add-to-cart; product details will be fetched lazily when reading cart
        }
        if (product != null) {
            if (product.getStockQuantity() < quantity) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
        }
        
        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cartId, productId);
        
        if (existingItem.isPresent()) {
            // Update quantity
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            // Create new cart item
            CartItem cartItem = CartItem.builder()
                    .cartId(cartId)
                    .productId(productId)
                    .quantity(quantity)
                    .build();
            cartItemRepository.save(cartItem);
        }
        
        // Refresh cart to get updated items
        cart = cartRepository.findById(cartId).orElseThrow();
        return convertToCartResponse(cart);
    }
    
    public CartResponse removeItemFromCart(String cartId, String productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + cartId));
        
        cartItemRepository.deleteByCartIdAndProductId(cartId, productId);
        
        // Refresh cart to get updated items
        cart = cartRepository.findById(cartId).orElseThrow();
        return convertToCartResponse(cart);
    }
    
    public CartResponse getCartByUserId(String userId) {
        // Use findFirst to handle potential duplicate carts
        Cart cart = cartRepository.findFirstByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
        
        return convertToCartResponse(cart);
    }
    
    public void clearCart(String cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + cartId));
        
        cartItemRepository.deleteByCartId(cartId);
    }
    
    private CartResponse convertToCartResponse(Cart cart) {
        // Fetch items by cartId to avoid relying on embedded items on Cart document
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getCartId());
        List<CartItemResponse> cartItemResponses = cartItems.stream()
                .map(this::convertToCartItemResponse)
                .collect(Collectors.toList());
        
        Double totalAmount = cartItemResponses.stream()
                .mapToDouble(CartItemResponse::getSubtotal)
                .sum();
        
        return CartResponse.builder()
                .cartId(cart.getCartId())
                .userId(cart.getUserId())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .cartItems(cartItemResponses)
                .totalAmount(totalAmount)
                .build();
    }
    
    private CartItemResponse convertToCartItemResponse(CartItem cartItem) {
        try {
            // Fetch product details from Product Service
            ProductResponse product = productServiceClient.getProductById(cartItem.getProductId(), "");
            
            if (product != null) {
                // Use discountPrice if available, otherwise use price
                Double productPrice = product.getDiscountPrice() != null ? 
                        product.getDiscountPrice().doubleValue() : 
                        product.getPrice().doubleValue();
                Double subtotal = productPrice * cartItem.getQuantity();
                
                return CartItemResponse.builder()
                        .cartItemId(cartItem.getCartItemId())
                        .productId(cartItem.getProductId())
                        .productName(product.getName())
                        .productPrice(productPrice)
                        .quantity(cartItem.getQuantity())
                        .subtotal(subtotal)
                        .product(product)  // Include full product object for frontend
                        .build();
            } else {
                // Fallback if product not found
                return CartItemResponse.builder()
                        .cartItemId(cartItem.getCartItemId())
                        .productId(cartItem.getProductId())
                        .productName("Product Not Found")
                        .productPrice(0.0)
                        .quantity(cartItem.getQuantity())
                        .subtotal(0.0)
                        .build();
            }
        } catch (Exception e) {
            // Fallback if there's an error fetching product
            return CartItemResponse.builder()
                    .cartItemId(cartItem.getCartItemId())
                    .productId(cartItem.getProductId())
                    .productName("Error Loading Product")
                    .productPrice(0.0)
                    .quantity(cartItem.getQuantity())
                    .subtotal(0.0)
                    .build();
        }
    }
}


