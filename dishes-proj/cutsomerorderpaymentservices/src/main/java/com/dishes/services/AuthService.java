package com.dishes.services;

import com.dishes.dtos.AuthResponse;
import com.dishes.entities.Customer;
import com.dishes.repositories.CustomerRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private LoggingService loggingService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public AuthResponse login(String email, String password) {
        loggingService.logInfo("Login attempt for email: " + email);
        
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent()) {
            loggingService.logInfo("Login successful for email: " + email);
            return new AuthResponse(true, "Login succeeded", generateJwtToken(customer.get()), 3600L);
        }
        
        loggingService.logError("Invalid credentials for email: " + email);
        return new AuthResponse(false, "Invalid credentials", null, null);
    }

    public String generateJwtToken(Customer customer) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

            return Jwts.builder()
                    .setSubject(Long.toString(customer.getId()))
                    .claim("id", customer.getId())
                    .claim("role", "CUSTOMER")
                    .claim("email", customer.getEmail())
                    .claim("name", customer.getName())
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        }
        catch (Exception e) {
            loggingService.logError("Failed to generate JWT token: " + e.getMessage());
            throw new RuntimeException("Token generation failed", e);
        }
    }

    public Customer validateToken(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            var claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Long userId = Long.parseLong(claims.getSubject());
            Customer customer = customerRepository.findById(userId).orElse(null);
            
            if (customer == null) {
                loggingService.logWarning("Token validation failed - user not found: " + userId);
            }
            
            return customer;
        }
        catch (Exception e) {
            loggingService.logError("Token validation failed: " + e.getMessage());
            return null;
        }
    }
}