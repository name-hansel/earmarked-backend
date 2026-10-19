package com.earmarked.earmark.dto;

import com.earmarked.earmark.dto.spotify.AlbumSummary;
import com.earmarked.earmark.entity.Earmark;

import java.time.Instant;
import java.util.UUID;

public record EarmarkResponse(UUID id, String spotifyAlbumId, String note, Instant createdAt, AlbumSummary album) {
    public static EarmarkResponse from(Earmark earmark) {
        return new EarmarkResponse(earmark.getId(), earmark.getSpotifyAlbumId(), earmark.getNote(),
                earmark.getCreatedAt(), null);
    }

    public EarmarkResponse withAlbum(AlbumSummary album) {
        return new EarmarkResponse(id, spotifyAlbumId, note, createdAt, album);
    }
}
