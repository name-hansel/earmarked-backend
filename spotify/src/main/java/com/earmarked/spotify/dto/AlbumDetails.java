package com.earmarked.spotify.dto;

import com.earmarked.spotify.dto.spotify.TrackDetails;

import java.util.List;

public record AlbumDetails(String id, String name, List<String> artists, String imageUrl, String releaseDate,
                           Integer totalTracks, String albumType, List<TrackDetails> tracks) {
}
