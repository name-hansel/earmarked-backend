package com.earmarked.earmark.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final RSAPublicKey publicKey;
    private static final String TYPE = "type";
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    public UUID extractUserId(String token) {
        Claims claims = getClaims(token);

        if (!ACCESS_TOKEN.equals(claims.get(TYPE, String.class))) {
            throw new JwtException("Token is not an access token");
        }

        return UUID.fromString(claims.getSubject());
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload();
    }
}
