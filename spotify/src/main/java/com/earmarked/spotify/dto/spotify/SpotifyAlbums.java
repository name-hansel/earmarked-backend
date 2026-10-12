package com.earmarked.spotify.dto.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyAlbums(List<SpotifyAlbum> items) {

}
