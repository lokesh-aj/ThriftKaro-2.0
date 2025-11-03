package com.productservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
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
        logger.debug("Auth header present: " + (authHeader != null));

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                boolean isValid = jwtUtil.validateToken(token);
                
                if (isValid) {
                    String email = jwtUtil.extractEmail(token);
                    String role = jwtUtil.extractRole(token);
                    logger.debug("Token valid. Email: " + email + ", Role: " + (role != null ? role : "null/not found"));

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
                    
                    // Store role in authentication details
                    WebAuthenticationDetailsSource detailsSource = new WebAuthenticationDetailsSource();
                    Map<String, Object> details = new HashMap<>();
                    details.put("role", role != null ? role : "USER"); // Default to USER if role is null
                    details.put("remoteAddress", detailsSource.buildDetails(request).getRemoteAddress());
                    details.put("sessionId", detailsSource.buildDetails(request).getSessionId());
                    authToken.setDetails(details);
                    
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("Authentication set successfully with role: " + details.get("role"));
                } else {
                    logger.warn("Token validation failed - token is invalid or expired");
                }
            } catch (Exception e) {
                logger.error("Exception during token validation: " + e.getMessage(), e);
            }
        }

        filterChain.doFilter(request, response);
    }
}
