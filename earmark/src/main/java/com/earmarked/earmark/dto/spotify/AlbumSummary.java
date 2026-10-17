package com.earmarked.earmark.dto.spotify;

import java.util.List;

public record AlbumSummary(String id, String name, List<String> artists, String imageUrl, String releaseDate,
                           String albumType) {
}
