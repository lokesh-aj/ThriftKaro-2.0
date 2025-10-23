package com.orderservice.repository;

import com.orderservice.entities.Withdraw;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawRepository extends MongoRepository<Withdraw, String> {
    List<Withdraw> findAllByOrderByCreatedAtDesc();
}

