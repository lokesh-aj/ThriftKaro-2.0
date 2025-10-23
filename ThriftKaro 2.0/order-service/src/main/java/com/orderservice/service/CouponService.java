package com.orderservice.service;

import com.orderservice.dto.CreateCouponRequest;
import com.orderservice.entities.CouponCode;
import com.orderservice.repository.CouponCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {
    
    private final CouponCodeRepository couponCodeRepository;
    
    public CouponCode createCoupon(CreateCouponRequest request) {
        // Check if coupon code already exists
        Optional<CouponCode> existingCoupon = couponCodeRepository.findByName(request.getName());
        if (existingCoupon.isPresent()) {
            throw new RuntimeException("Coupon code already exists!");
        }
        
        CouponCode coupon = CouponCode.builder()
                .name(request.getName())
                .value(request.getValue())
                .minAmount(request.getMinAmount())
                .maxAmount(request.getMaxAmount())
                .shopId(request.getShopId())
                .selectedProduct(request.getSelectedProduct())
                .build();
        
        return couponCodeRepository.save(coupon);
    }
    
    public List<CouponCode> getCouponsByShopId(String shopId) {
        return couponCodeRepository.findByShopId(shopId);
    }
    
    public void deleteCoupon(String id) {
        couponCodeRepository.deleteById(id);
    }
    
    public Optional<CouponCode> getCouponByName(String name) {
        return couponCodeRepository.findByName(name);
    }
}

