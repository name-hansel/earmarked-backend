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
    private static final String KEY_PREFIX = "account:activation:";

    private final StringRedisTemplate redisTemplate;

    private final SecureRandom secureRandom;

    public String generateAccountActivationToken(UUID userId) {
        String token = generateSecureToken();
        String key = KEY_PREFIX + token;
        redisTemplate.opsForValue().set(
                key,
                userId.toString(),
                TOKEN_TTL
        );

        return token;
    }

    public Optional<UUID> validateToken(String token) {
        String key = KEY_PREFIX + token;
        String userId = redisTemplate.opsForValue().get(key);

        if (userId == null) {
            return Optional.empty();
        }

        redisTemplate.delete(key);
        return Optional.of(UUID.fromString(userId));
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
