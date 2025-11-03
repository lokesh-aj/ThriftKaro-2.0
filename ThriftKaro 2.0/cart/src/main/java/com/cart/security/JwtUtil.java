package com.cart.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationThatIsLongEnoughForSecurityRequirements}")
    private String secret;
    
    @Value("${jwt.expiration:86400000}")
    private Long expiration;
    
    @jakarta.annotation.PostConstruct
    public void init() {
        // Log the secret length and first/last chars (for debugging without exposing full secret)
        System.out.println("========================================");
        System.out.println("JWT Secret configured - Length: " + (secret != null ? secret.length() : 0));
        if (secret != null && secret.length() > 0) {
            System.out.println("JWT Secret first 10 chars: " + secret.substring(0, Math.min(10, secret.length())));
            System.out.println("JWT Secret last 10 chars: " + secret.substring(Math.max(0, secret.length() - 10)));
            // Expected UserService secret: "mySecretKeyForJWTTokenGenerationThatIsLongEnoughForSecurityRequirements"
            String expectedSecret = "mySecretKeyForJWTTokenGenerationThatIsLongEnoughForSecurityRequirements";
            if (secret.equals(expectedSecret)) {
                System.out.println("✅ JWT Secret MATCHES UserService secret!");
            } else {
                System.err.println("❌ JWT Secret DOES NOT MATCH UserService secret!");
                System.err.println("   Expected length: " + expectedSecret.length());
                System.err.println("   Actual length: " + secret.length());
            }
        } else {
            System.err.println("❌ JWT Secret is NULL or EMPTY!");
        }
        System.out.println("========================================");
    }
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public String extractUserId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            
            // Debug: Print all claims (commented out - uncomment if needed for debugging)
            // System.out.println("=== Token Claims Debug ===");
            // System.out.println("Subject (email): " + claims.getSubject());
            // System.out.println("All claim keys: " + claims.keySet());
            
            Object userIdClaim = claims.get("userId");
            if (userIdClaim == null) {
                System.err.println("WARNING: userId claim not found in token!");
                System.err.println("This means the token was generated without userId. User needs to log in again.");
                return null;
            }
            // Handle both String and Long (for backward compatibility)
            if (userIdClaim instanceof String) {
                System.out.println("Extracted userId as String: " + userIdClaim);
                return (String) userIdClaim;
            } else if (userIdClaim instanceof Long) {
                System.out.println("Extracted userId as Long: " + userIdClaim);
                return userIdClaim.toString();
            } else {
                System.out.println("Extracted userId as other type: " + userIdClaim + " (type: " + userIdClaim.getClass().getSimpleName() + ")");
                return userIdClaim.toString();
            }
        } catch (Exception e) {
            System.err.println("Error extracting userId from token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (io.jsonwebtoken.security.SignatureException e) {
            System.err.println("JWT Signature mismatch! Secret length: " + secret.length());
            System.err.println("This usually means the JWT secret doesn't match UserService secret.");
            throw e;
        } catch (Exception e) {
            System.err.println("Error parsing JWT token: " + e.getMessage());
            throw e;
        }
    }
    
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
    
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}


