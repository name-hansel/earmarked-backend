package com.earmarked.spotify.dto.spotify.track;

import com.earmarked.spotify.dto.spotify.SpotifyArtist;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyTrack(String id, String name, @JsonProperty("track_number") Integer trackNumber,
                           @JsonProperty("duration_ms") Long durationMs, List<SpotifyArtist> artists) {
}
