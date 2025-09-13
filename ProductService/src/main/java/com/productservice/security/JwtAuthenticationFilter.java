package com.productservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        logger.info("Auth header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logger.info("Extracted token: " + token.substring(0, Math.min(20, token.length())) + "...");
            try {
                if (jwtUtil.validateToken(token)) {
                    String email = jwtUtil.extractEmail(token);
                    String role = jwtUtil.extractRole(token);
                    logger.info("Token valid. Email: " + email + ", Role: " + role);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
                    
                    // Store role in authentication details
                    WebAuthenticationDetailsSource detailsSource = new WebAuthenticationDetailsSource();
                    Map<String, Object> details = new HashMap<>();
                    details.put("role", role);
                    details.put("remoteAddress", detailsSource.buildDetails(request).getRemoteAddress());
                    details.put("sessionId", detailsSource.buildDetails(request).getSessionId());
                    authToken.setDetails(details);
                    
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("Authentication set successfully");
                } else {
                    logger.warn("Token validation failed");
                }
            } catch (Exception e) {
                // Invalid token, ignore and proceed (will be caught by Spring Security)
                logger.warn("Invalid JWT token: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            logger.info("No valid auth header found");
        }

        filterChain.doFilter(request, response);
    }
}