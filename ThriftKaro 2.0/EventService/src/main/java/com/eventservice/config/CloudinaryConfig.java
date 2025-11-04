package com.eventservice.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    
    @Value("${cloudinary.cloud-name:}")
    private String cloudName;
    
    @Value("${cloudinary.api-key:}")
    private String apiKey;
    
    @Value("${cloudinary.api-secret:}")
    private String apiSecret;
    
    @Bean
    public Cloudinary cloudinary() {
        // Only create Cloudinary if credentials are provided
        // If not configured, create a dummy instance that will fail gracefully
        if (cloudName == null || cloudName.isEmpty() || 
            apiKey == null || apiKey.isEmpty() || 
            apiSecret == null || apiSecret.isEmpty()) {
            // Return a Cloudinary instance with empty credentials (will fail gracefully)
            return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "",
                "api_key", "",
                "api_secret", ""
            ));
        }
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret
        ));
    }
}


