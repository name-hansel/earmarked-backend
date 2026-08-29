package com.earmarked.authentication.repository;

import com.earmarked.authentication.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    void deleteAllByUserId(UUID userId);

    @Modifying
    @Query("""
                DELETE FROM RefreshTokenEntity r
                WHERE r.userId = :userId
                  AND r.id <> :currentTokenId
            """)
    void deleteAllExcept(@Param("userId") UUID userId, @Param("currentTokenId") UUID currentTokenId);
}
