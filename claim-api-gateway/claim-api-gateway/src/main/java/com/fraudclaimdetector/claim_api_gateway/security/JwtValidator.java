package com.fraudclaimdetector.claim_api_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

@Component
public class JwtValidator {

    private static final String SECRET =
            "this-is-a-very-long-secret-key-at-least-32-chars-for-claim-fraud-system-2025";

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}