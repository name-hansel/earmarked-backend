package com.earmarked.authentication.service;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String TYPE = "type";
    private final RSAPrivateKey privateKey;

    @Value("${jwt.access-token-expiration-seconds}")
    private long accessTokenExpirationSeconds;

    @Value("${jwt.refresh-token-expiration-seconds}")
    private long refreshTokenExpirationSeconds;

    private enum TokenType {
        ACCESS_TOKEN,
        REFRESH_TOKEN
    }

    public String generateAccessToken(UUID userUuid) {
        return generateToken(userUuid, TokenType.ACCESS_TOKEN, null);
    }

    public String generateRefreshToken(UUID userUuid, UUID refreshTokenUuid) {
        return generateToken(userUuid, TokenType.REFRESH_TOKEN, refreshTokenUuid);
    }

    private String generateToken(UUID userId, TokenType tokenType, UUID refreshTokenUuid) {
        Instant now = Instant.now();
        JwtBuilder builder = Jwts.builder().subject(userId.toString()).claim(TYPE, tokenType)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(getExpirationSeconds(tokenType)))).signWith(privateKey);

        if (refreshTokenUuid != null) {
            builder.id(refreshTokenUuid.toString());
        }

        return builder.compact();
    }

    private long getExpirationSeconds(TokenType tokenType) {
        switch (tokenType) {
            case ACCESS_TOKEN -> {
                return accessTokenExpirationSeconds;
            }
            case REFRESH_TOKEN -> {
                return refreshTokenExpirationSeconds;
            }
            default -> throw new RuntimeException("Unexpected token type: " + tokenType);
        }
    }
}
