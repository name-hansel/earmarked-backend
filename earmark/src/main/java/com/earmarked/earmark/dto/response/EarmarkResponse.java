package com.earmarked.earmark.dto.response;

import com.earmarked.earmark.dto.spotify.AlbumSummary;
import com.earmarked.earmark.entity.EarmarkEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record EarmarkResponse(UUID id, String spotifyAlbumId, String note, Instant createdAt, AlbumSummary album,
                              LocalDate listenedAt, boolean listened, Short rating, String review) {
    public static EarmarkResponse from(EarmarkEntity earmarkEntity) {
        return new EarmarkResponse(earmarkEntity.getId(), earmarkEntity.getSpotifyAlbumId(), earmarkEntity.getNote(),
                earmarkEntity.getCreatedAt(), null, earmarkEntity.getListenedAt(), earmarkEntity.isListened(),
                earmarkEntity.getRating(), earmarkEntity.getReview());
    }

    public EarmarkResponse withAlbum(AlbumSummary album) {
        return new EarmarkResponse(id, spotifyAlbumId, note, createdAt, album, listenedAt, listened, rating, review);
    }
}
