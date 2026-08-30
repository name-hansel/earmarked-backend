package com.earmarked.authentication.service.token;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountActivationTokenService {
    private static final Duration TOKEN_TTL = Duration.ofHours(24);
    private static final String KEY_PREFIX_USER = "account:activation:user:";
    private static final String KEY_PREFIX_TOKEN = "account:activation:token:";

    private final StringRedisTemplate redisTemplate;
    private final SecureRandom secureRandom;

    public String generateAccountActivationToken(UUID userId) {
        // Clear any existing tokens
        invalidateExistingTokens(userId);

        String token = generateSecureToken();
        addTokenToRedis(userId.toString(), token);

        return token;
    }

    public Optional<UUID> validateToken(String token) {
        String tokenKey = getTokenKey(token);
        String userUuid = redisTemplate.opsForValue().get(tokenKey);

        if (userUuid == null) {
            return Optional.empty();
        }

        cleanRedisEntries(getUserKey(userUuid), tokenKey);
        return Optional.of(UUID.fromString(userUuid));
    }

    private String getUserKey(String userUuid) {
        return KEY_PREFIX_USER + userUuid;
    }

    private String getTokenKey(String token) {
        return KEY_PREFIX_TOKEN + token;
    }

    private void addTokenToRedis(String userUuid, String token) {
        String userKey = getUserKey(userUuid);
        redisTemplate.opsForValue().set(userKey, token, TOKEN_TTL);

        String tokenKey = getTokenKey(token);
        redisTemplate.opsForValue().set(tokenKey, userUuid, TOKEN_TTL);
    }

    private void invalidateExistingTokens(UUID userId) {
        String userKey = getUserKey(userId.toString());
        String token = redisTemplate.opsForValue().get(userKey);

        cleanRedisEntries(userKey, getTokenKey(token));
    }

    private void cleanRedisEntries(String userKey, String tokenKey) {
        redisTemplate.delete(userKey);
        if (tokenKey != null) {
            redisTemplate.delete(tokenKey);
        }
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
