package com.earmarked.authentication.repository;

import com.earmarked.authentication.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    void deleteAllByUserId(UUID userId);
}
