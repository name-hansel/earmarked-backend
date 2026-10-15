package com.earmarked.earmark.repository;

import com.earmarked.earmark.entity.Earmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EarmarkRepository extends JpaRepository<Earmark, UUID> {
    boolean existsByUserIdAndSpotifyAlbumId(UUID userId, String spotifyAlbumId);
}
