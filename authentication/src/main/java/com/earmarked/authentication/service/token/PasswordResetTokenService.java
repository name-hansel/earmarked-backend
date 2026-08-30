package com.earmarked.authentication.service.token;

import com.earmarked.authentication.dto.result.TokenConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;

@Service
public class PasswordResetTokenService extends TokenService {
    private static final TokenConfiguration TOKEN_CONFIGURATION = new TokenConfiguration("password:reset:user",
            "password:reset:token:", Duration.ofMinutes(30));

    public PasswordResetTokenService(SecureRandom secureRandom, StringRedisTemplate redisTemplate) {
        super(secureRandom, redisTemplate);
    }

    public String generatePasswordResetToken(UUID userId) {
        return generateToken(userId);
    }

    @Override
    protected TokenConfiguration getTokenConfiguration() {
        return TOKEN_CONFIGURATION;
    }
}
