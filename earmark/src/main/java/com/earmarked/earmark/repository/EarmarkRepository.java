package com.earmarked.earmark.repository;

import com.earmarked.earmark.entity.Earmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EarmarkRepository extends JpaRepository<Earmark, UUID> {
    boolean existsByUserIdAndSpotifyAlbumId(UUID userId, String spotifyAlbumId);

    Page<Earmark> findByUserId(UUID userId, Pageable pageable);

    long deleteByIdAndUserId(UUID id, UUID userId);

    Optional<Earmark> findByUserIdAndSpotifyAlbumId(UUID userId, String spotifyAlbumId);

    Optional<Earmark> findByIdAndUserId(UUID id, UUID userId);

    @Query("""
            select e from Earmark e
            where e.userId = :userId
              and (lower(e.albumName) like :pattern escape '\\'
                or lower(e.albumArtists) like :pattern escape '\\'
                or lower(e.note) like :pattern escape '\\')
            """)
    Page<Earmark> search(@Param("userId") UUID userId, @Param("pattern") String pattern, Pageable pageable);
}
