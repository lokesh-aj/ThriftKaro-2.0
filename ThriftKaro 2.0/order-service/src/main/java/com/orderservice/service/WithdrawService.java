package com.orderservice.service;

import com.orderservice.dto.CreateWithdrawRequest;
import com.orderservice.entities.Withdraw;
import com.orderservice.repository.WithdrawRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawService {
    
    private final WithdrawRepository withdrawRepository;
    
    public Withdraw createWithdrawRequest(CreateWithdrawRequest request, Map<String, Object> seller) {
        Withdraw withdraw = Withdraw.builder()
                .seller(seller)
                .amount(request.getAmount())
                .status("Processing")
                .build();
        
        return withdrawRepository.save(withdraw);
    }
    
    public List<Withdraw> getAllWithdraws() {
        return withdrawRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public Withdraw updateWithdrawStatus(String id, String status) {
        Withdraw withdraw = withdrawRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Withdraw not found"));
        
        withdraw.setStatus(status);
        withdraw.setUpdatedAt(java.time.LocalDateTime.now());
        
        return withdrawRepository.save(withdraw);
    }
}

