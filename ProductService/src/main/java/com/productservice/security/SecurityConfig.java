package com.productservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Allow health check and debug endpoints
                        .requestMatchers("/actuator/**", "/products/health", "/products/debug", "/products/test-jwt").permitAll()
                        // Public endpoints - no authentication required
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/products", "/products/*").permitAll()
                        // Seller-only endpoints - require authentication
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/products").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/products/*/stock").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/products/*").authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}