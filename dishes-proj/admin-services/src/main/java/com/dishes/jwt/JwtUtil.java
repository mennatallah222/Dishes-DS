package com.dishes.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Date;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;


@ApplicationScoped
public class JwtUtil {
    private final String SECRET_STRING="THIS-IS_DISHES-STRONG_SECRETKEY-for-256-bit-secret-key-1234567890";
    SecretKey SECRET_KEY= Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    public String generateToken(Long sellerId, String companyName, String sellerEmail) {
        return Jwts.builder()
            .setSubject(sellerId.toString())
            .claim("sellerId", sellerId)
            .claim("role", "SELLER")
            .claim("email", sellerEmail)
            .claim("companyName", companyName)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
            .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
            .compact();

    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}