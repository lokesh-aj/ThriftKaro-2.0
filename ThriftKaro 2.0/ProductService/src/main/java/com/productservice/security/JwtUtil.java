package com.productservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    private final Key key;
    private final long expiration;

    public JwtUtil(@Value("${jwt.secret:mySecretKey123456789012345678901234567890}") String secret, 
                   @Value("${jwt.expiration:86400000}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    // Generate JWT token (if needed for testing)
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    // Validate token and get email
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Extract role from token
    public String extractRole(String token) {
        try {
            Object roleClaim = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role");
            
            if (roleClaim != null) {
                String role = roleClaim.toString();
                System.out.println("Successfully extracted role from token: " + role);
                return role;
            }
            System.out.println("Warning: Token does not contain 'role' claim");
            return null;
        } catch (Exception e) {
            System.err.println("Error extracting role from token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Validate if token is valid
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.err.println("Token validation failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Extract username (same as email for this service)
    public String extractUsername(String token) {
        return extractEmail(token);
    }

    // Extract user ID from token
    public Long extractUserId(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userId", Long.class);
        } catch (Exception e) {
            return null;
        }
    }
}
