package com.earmarked.spotify.dto;

import java.util.List;

public record AlbumSummary(String id, String name, List<String> artists, List<AlbumImage> images, String releaseDate,
                           String releaseDatePrecision,
                           String albumType, String spotifyUrl) {
}
