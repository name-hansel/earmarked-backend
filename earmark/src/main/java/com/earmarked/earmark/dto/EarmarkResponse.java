package com.earmarked.earmark.dto;

import com.earmarked.earmark.entity.Earmark;

import java.time.Instant;
import java.util.UUID;

public record EarmarkResponse(UUID id, String spotifyAlbumId, String note, Instant createdAt) {

    public static EarmarkResponse from(Earmark earmark) {
        return new EarmarkResponse(earmark.getId(), earmark.getSpotifyAlbumId(), earmark.getNote(),
                earmark.getCreatedAt());
    }
}
