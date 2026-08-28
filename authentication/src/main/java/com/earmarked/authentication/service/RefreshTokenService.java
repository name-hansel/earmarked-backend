package com.earmarked.authentication.service;

import com.earmarked.authentication.config.jwt.JwtProperties;
import com.earmarked.authentication.entity.RefreshTokenEntity;
import com.earmarked.authentication.exception.InvalidRefreshTokenException;
import com.earmarked.authentication.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public UUID findValidTokenUserId(UUID refreshTokenId) {
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findById(refreshTokenId)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (refreshTokenEntity.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidRefreshTokenException();
        }

        return refreshTokenEntity.getUserId();
    }

    @Transactional
    public UUID rotate(UUID oldRefreshTokenUuid) {
        UUID userId = findValidTokenUserId(oldRefreshTokenUuid);
        revoke(oldRefreshTokenUuid);
        
        return createRefreshToken(userId);
    }
}
