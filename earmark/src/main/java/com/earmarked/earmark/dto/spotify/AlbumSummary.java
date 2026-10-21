package com.earmarked.earmark.dto.spotify;

import java.util.List;

public record AlbumSummary(String id, String name, List<String> artists, List<AlbumImage> images, String releaseDate,
                           String albumType, String spotifyUrl) {
}
