package com.earmarked.authentication.service;

import com.earmarked.authentication.config.jwt.JwtProperties;
import com.earmarked.authentication.entity.RefreshTokenEntity;
import com.earmarked.authentication.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;

    public UUID createRefreshToken(UUID userId) {
        RefreshTokenEntity refreshToken = new RefreshTokenEntity();

        refreshToken.setUserId(userId);
        refreshToken.setExpiresAt(Instant.now().plusSeconds(jwtProperties.refreshTokenExpirationSeconds()));

        RefreshTokenEntity savedRefreshToken = refreshTokenRepository.save(refreshToken);

        return savedRefreshToken.getId();
    }

    public void revoke(UUID refreshTokenId) {
        refreshTokenRepository.deleteById(refreshTokenId);
    }
}
