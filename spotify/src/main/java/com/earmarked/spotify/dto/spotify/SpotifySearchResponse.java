package com.earmarked.spotify.dto.spotify;

import com.earmarked.spotify.dto.spotify.album.SpotifyAlbums;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifySearchResponse(SpotifyAlbums albums) {
}
