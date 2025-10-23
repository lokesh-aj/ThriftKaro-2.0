package com.notificationservice.service;

import com.notificationservice.dto.EmailRequest;
import com.notificationservice.dto.OrderConfirmationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    public void sendEmail(EmailRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(request.getEmail());
            message.setSubject(request.getSubject());
            message.setText(request.getMessage());
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", request.getEmail());
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", request.getEmail(), e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
    
    public void sendWithdrawRequestEmail(String email, String sellerName, Double amount) {
        String subject = "Withdraw Request";
        String message = String.format("Hello %s, Your withdraw request of $%.2f is processing. It will take 3days to 7days to processing!", 
                sellerName, amount);
        
        EmailRequest emailRequest = EmailRequest.builder()
                .email(email)
                .subject(subject)
                .message(message)
                .build();
        
        sendEmail(emailRequest);
    }
    
    public void sendPaymentConfirmationEmail(String email, String sellerName, Double amount) {
        String subject = "Payment confirmation";
        String message = String.format("Hello %s, Your withdraw request of $%.2f is on the way. Delivery time depends on your bank's rules it usually takes 3days to 7days.", 
                sellerName, amount);
        
        EmailRequest emailRequest = EmailRequest.builder()
                .email(email)
                .subject(subject)
                .message(message)
                .build();
        
        sendEmail(emailRequest);
    }
    
    public void sendOrderConfirmationEmail(OrderConfirmationRequest request) {
        String subject = "Order Confirmation - ThriftKaro";
        StringBuilder messageBuilder = new StringBuilder();
        
        messageBuilder.append("Dear Customer,\n\n");
        messageBuilder.append("Thank you for your order! Your order has been successfully placed.\n\n");
        messageBuilder.append("Order Details:\n");
        messageBuilder.append("Order ID: ").append(request.getOrderId()).append("\n");
        messageBuilder.append("Status: ").append(request.getStatus()).append("\n");
        messageBuilder.append("Total Amount: $").append(String.format("%.2f", request.getTotalPrice())).append("\n\n");
        
        messageBuilder.append("Items Ordered:\n");
        for (Map<String, Object> item : request.getOrderDetails()) {
            messageBuilder.append("- ").append(item.get("name")).append(" (Qty: ").append(item.get("qty")).append(")\n");
        }
        
        messageBuilder.append("\nShipping Address:\n");
        Map<String, Object> address = request.getShippingAddress();
        messageBuilder.append(address.get("address1")).append("\n");
        if (address.get("address2") != null) {
            messageBuilder.append(address.get("address2")).append("\n");
        }
        messageBuilder.append(address.get("city")).append(", ").append(address.get("state")).append(" ").append(address.get("zipCode")).append("\n");
        messageBuilder.append(address.get("country")).append("\n\n");
        
        messageBuilder.append("We will send you another email when your order ships.\n\n");
        messageBuilder.append("Thank you for shopping with ThriftKaro!\n\n");
        messageBuilder.append("Best regards,\n");
        messageBuilder.append("ThriftKaro Team");
        
        EmailRequest emailRequest = EmailRequest.builder()
                .email(request.getUserEmail())
                .subject(subject)
                .message(messageBuilder.toString())
                .build();
        
        sendEmail(emailRequest);
        log.info("Order confirmation email sent successfully for order ID: {}", request.getOrderId());
    }
}

