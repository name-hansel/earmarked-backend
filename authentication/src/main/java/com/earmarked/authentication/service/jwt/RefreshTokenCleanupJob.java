package com.earmarked.authentication.service.jwt;

import com.earmarked.authentication.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupJob {
    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "${authentication.refresh-token-cleanup.cron:0 0 3 * * SUN}", zone = "UTC")
    @Transactional
    public void deleteExpiredRefreshTokens() {
        int deleted = refreshTokenRepository.deleteExpired(Instant.now());
        log.info("Refresh token cleanup removed {} expired tokens", deleted);
    }

}
