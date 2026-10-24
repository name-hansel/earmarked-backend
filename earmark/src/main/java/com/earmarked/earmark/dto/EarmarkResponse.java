package com.earmarked.earmark.dto;

import com.earmarked.earmark.dto.spotify.AlbumSummary;
import com.earmarked.earmark.entity.Earmark;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record EarmarkResponse(UUID id, String spotifyAlbumId, String note, Instant createdAt, AlbumSummary album,
                              LocalDate listenedAt) {
    public static EarmarkResponse from(Earmark earmark) {
        return new EarmarkResponse(earmark.getId(), earmark.getSpotifyAlbumId(), earmark.getNote(),
                earmark.getCreatedAt(), null, earmark.getListenedAt());
    }

    public EarmarkResponse withAlbum(AlbumSummary album) {
        return new EarmarkResponse(id, spotifyAlbumId, note, createdAt, album, listenedAt);
    }
}
