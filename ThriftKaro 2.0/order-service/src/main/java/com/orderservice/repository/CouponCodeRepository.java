package com.orderservice.repository;

import com.orderservice.entities.CouponCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponCodeRepository extends MongoRepository<CouponCode, String> {
    Optional<CouponCode> findByName(String name);
    List<CouponCode> findByShopId(String shopId);
}

