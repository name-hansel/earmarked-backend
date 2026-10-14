package com.earmarked.spotify.dto.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyAlbum(String id, String name, List<SpotifyArtist> artists, List<SpotifyImage> images,
                           @JsonProperty("release_date") String releaseDate,
                           @JsonProperty("total_tracks") Integer totalTracks,
                           @JsonProperty("album_type") String albumType, SpotifyTracks tracks) {
}
