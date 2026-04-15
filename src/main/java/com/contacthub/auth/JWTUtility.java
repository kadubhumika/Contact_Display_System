package com.contacthub.auth;

import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey; // Required for verifyWith
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
@Component
public class JWTUtility {


    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        // Import java.nio.charset.StandardCharsets
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email){
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractEmail(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
