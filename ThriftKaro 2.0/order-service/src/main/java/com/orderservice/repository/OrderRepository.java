package com.orderservice.repository;

import com.orderservice.entities.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
    // user is stored as a Map; query nested key user._id
    @Query("{'user._id': ?0}")
    List<Order> findByUserId(String userId);
    
    List<Order> findByStatus(String status);
    
    // Match both user._id and status
    @Query("{'user._id': ?0, 'status': ?1}")
    List<Order> findByUserIdAndStatus(String userId, String status);
    
    // cart is a list of maps; match any element with shopId
    @Query("{'cart.shopId': ?0}")
    List<Order> findByCartShopId(String shopId);
    
    List<Order> findAllByOrderByDeliveredAtDescCreatedAtDesc();
}

