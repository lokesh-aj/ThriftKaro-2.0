package com.productservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Health check and debug endpoints - no authentication required
                        .requestMatchers("/actuator/**", "/api/v2/product/health", "/api/v2/product/debug", "/api/v2/product/test-jwt").permitAll()
                        // Public GET endpoints - no authentication required
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v2/product/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/products/**").permitAll() // Compatibility with cart service
                        // Seller-only endpoints - require authentication
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v2/product/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/v2/product/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/v2/product/**").authenticated()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response,
                                 AuthenticationException authException) throws IOException {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                
                Map<String, Object> body = new HashMap<>();
                body.put("success", false);
                body.put("message", "Authentication required. Invalid or expired JWT token.");
                body.put("error", "AUTHENTICATION_REQUIRED");
                body.put("details", authException.getMessage());
                
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(response.getOutputStream(), body);
            }
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response,
                              AccessDeniedException accessDeniedException) throws IOException {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                
                Map<String, Object> body = new HashMap<>();
                body.put("success", false);
                body.put("message", "Access denied. Insufficient permissions.");
                body.put("error", "ACCESS_DENIED");
                body.put("details", accessDeniedException.getMessage());
                
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(response.getOutputStream(), body);
            }
        };
    }
}
