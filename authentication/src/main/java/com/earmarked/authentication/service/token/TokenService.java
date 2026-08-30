package com.earmarked.authentication.service.token;

import com.earmarked.authentication.dto.result.TokenConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class TokenService {
    private final SecureRandom secureRandom;
    private final StringRedisTemplate redisTemplate;

    protected String generateToken(UUID userId) {
        // Remove existing entries for user
        invalidateExistingTokens(userId);

        String token = generateSecureToken();
        addTokenToRedis(userId, token);

        return token;
    }

    public Optional<UUID> consumeToken(String token) {
        TokenConfiguration tokenConfiguration = getTokenConfiguration();

        String tokenKey = getTokenKey(tokenConfiguration, token);
        String userUuid = redisTemplate.opsForValue().getAndDelete(tokenKey);

        if (userUuid == null) {
            return Optional.empty();
        }

        UUID userId = UUID.fromString(userUuid);
        redisTemplate.delete(getUserKey(tokenConfiguration, userId));

        return Optional.of(userId);
    }

    protected abstract TokenConfiguration getTokenConfiguration();

    protected void addTokenToRedis(UUID userUuid, String token) {
        TokenConfiguration tokenConfiguration = getTokenConfiguration();
        Duration tokenTtl = tokenConfiguration.tokenTtl();

        String userKey = getUserKey(tokenConfiguration, userUuid);
        redisTemplate.opsForValue().set(userKey, token, tokenTtl);

        String tokenKey = getTokenKey(tokenConfiguration, token);
        redisTemplate.opsForValue().set(tokenKey, userUuid.toString(), tokenTtl);
    }

    protected void invalidateExistingTokens(UUID userId) {
        TokenConfiguration tokenConfiguration = getTokenConfiguration();

        String userKey = getUserKey(tokenConfiguration, userId);
        String token = redisTemplate.opsForValue().getAndDelete(userKey);

        if (token == null) {
            return;
        }

        redisTemplate.delete(getTokenKey(tokenConfiguration, token));
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String getUserKey(TokenConfiguration tokenConfiguration, UUID userId) {
        return tokenConfiguration.keyPrefixUser() + userId.toString();
    }

    private String getTokenKey(TokenConfiguration tokenConfiguration, String token) {
        return tokenConfiguration.keyPrefixToken() + token;
    }
}
