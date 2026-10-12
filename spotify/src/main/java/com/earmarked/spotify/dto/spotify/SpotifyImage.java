package com.earmarked.spotify.dto.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyImage(String url, Integer width, Integer height) {
}
