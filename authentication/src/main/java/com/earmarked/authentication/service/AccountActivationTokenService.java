package com.earmarked.authentication.service;

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

    public String generateAccountActivationToken(UUID userUuid) {
        String token = generateSecureToken();
        addTokenToRedis(userUuid.toString(), token);

        return token;
    }

    public Optional<UUID> validateToken(String token) {
        String tokenKey = getTokenKey(token);
        String userUuid = redisTemplate.opsForValue().get(tokenKey);

        if (userUuid == null) {
            return Optional.empty();
        }

        cleanRedisEntries(token, userUuid);
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

    private void cleanRedisEntries(String token, String userUuid) {
        redisTemplate.delete(getTokenKey(token));
        redisTemplate.delete(getUserKey(userUuid));
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
