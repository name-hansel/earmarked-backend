package com.earmarked.authentication.service;

import com.earmarked.authentication.config.jwt.JwtConfiguration;
import com.earmarked.authentication.config.jwt.JwtProperties;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
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
    private final JwtProperties jwtProperties;

    public String generateAccessToken(UUID userUuid) {
        return generateToken(userUuid, JwtConfiguration.TokenType.ACCESS_TOKEN, null);
    }

    public String generateRefreshToken(UUID userUuid, UUID refreshTokenUuid) {
        return generateToken(userUuid, JwtConfiguration.TokenType.REFRESH_TOKEN, refreshTokenUuid);
    }

    private String generateToken(UUID userId, JwtConfiguration.TokenType tokenType, UUID refreshTokenUuid) {
        Instant now = Instant.now();
        JwtBuilder builder = Jwts.builder().subject(userId.toString()).claim(TYPE, tokenType)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(getExpirationSeconds(tokenType)))).signWith(privateKey);

        if (refreshTokenUuid != null) {
            builder.id(refreshTokenUuid.toString());
        }

        return builder.compact();
    }

    private long getExpirationSeconds(JwtConfiguration.TokenType tokenType) {
        switch (tokenType) {
            case ACCESS_TOKEN -> {
                return jwtProperties.accessTokenExpirationSecond();
            }
            case REFRESH_TOKEN -> {
                return jwtProperties.refreshTokenExpirationSecond();
            }
            default -> throw new RuntimeException("Unexpected token type: " + tokenType);
        }
    }
}
