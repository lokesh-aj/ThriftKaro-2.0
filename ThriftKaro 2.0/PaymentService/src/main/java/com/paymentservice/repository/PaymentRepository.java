package com.paymentservice.repository;

import com.paymentservice.entities.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    
    @Query(value = "{'orderId': ?0}", sort = "{'timestamp': -1}")
    Optional<Payment> findTopByOrderIdOrderByTimestampDesc(String orderId);
    
    List<Payment> findByUserId(String userId);
    
    List<Payment> findByStatus(String status);
}




