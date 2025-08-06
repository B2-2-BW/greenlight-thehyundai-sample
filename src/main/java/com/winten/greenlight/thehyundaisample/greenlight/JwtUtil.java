package com.winten.greenlight.thehyundaisample.greenlight;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winten.greenlight.thehyundaisample.greenlight.dto.Customer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    private final ObjectMapper objectMapper;

    public Customer getCustomerFromToken(String token) {
        Claims claims = extractAllClaims(token);
        try {
            return objectMapper.convertValue(claims, Customer.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}