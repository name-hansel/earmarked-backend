package com.earmarked.spotify.service;

import com.earmarked.spotify.config.spotify.SpotifyApi;
import com.earmarked.spotify.dto.spotify.SpotifySearchResponse;
import com.earmarked.spotify.dto.spotify.album.SpotifyAlbumFull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpotifyClient {
    private final SpotifyApi spotifyApi;

    public SpotifySearchResponse searchAlbums(String query) {
        log.info("Calling Spotify: search albums '{}'", query);
        return spotifyApi.search(query, "album", 5);
    }

    public SpotifyAlbumFull getAlbum(String id) {
        log.info("Calling Spotify: GET /albums/{}", id);
        return spotifyApi.getAlbum(id);
    }
}
