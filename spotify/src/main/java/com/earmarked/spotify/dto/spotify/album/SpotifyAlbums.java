package com.earmarked.spotify.dto.spotify.album;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyAlbums(List<SpotifyAlbum> items) {

}
