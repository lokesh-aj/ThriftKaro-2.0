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
        // Check if cart already exists for user
        Optional<Cart> existingCart = cartRepository.findByUserId(userId);
        if (existingCart.isPresent()) {
            return convertToCartResponse(existingCart.get());
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
        ProductResponse product = productServiceClient.getProductById(productId, token);
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + productId);
        }
        
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
        
        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartCartIdAndProductId(cartId, productId);
        
        if (existingItem.isPresent()) {
            // Update quantity
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            // Create new cart item
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
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
        
        cartItemRepository.deleteByCartCartIdAndProductId(cartId, productId);
        
        // Refresh cart to get updated items
        cart = cartRepository.findById(cartId).orElseThrow();
        return convertToCartResponse(cart);
    }
    
    public CartResponse getCartByUserId(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
        
        return convertToCartResponse(cart);
    }
    
    public void clearCart(String cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + cartId));
        
        cartItemRepository.deleteByCartCartId(cartId);
    }
    
    private CartResponse convertToCartResponse(Cart cart) {
        List<CartItemResponse> cartItemResponses = cart.getCartItems().stream()
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


