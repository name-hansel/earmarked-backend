package com.earmarked.spotify.dto;

import java.util.List;

public record AlbumDetails(String id, String name, List<String> artists, String imageUrl, String releaseDate,
                           Integer totalTracks, String albumType, List<TrackDetails> tracks) {
}
