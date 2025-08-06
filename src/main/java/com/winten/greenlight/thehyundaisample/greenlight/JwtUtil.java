package com.winten.greenlight.thehyundaisample.greenlight;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.winten.greenlight.thehyundaisample.greenlight.dto.Customer;
import com.winten.greenlight.thehyundaisample.greenlight.dto.WaitStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {
    @Value("${")
    private static String secret = "GreenlightSecret2e12secrethahahahsdhadhasdgreenlight!!!";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static Customer getCustomerFromToken(String token) {
        Claims claims = extractAllClaims(token);
        try {
            return objectMapper.convertValue(claims, Customer.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private static SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}