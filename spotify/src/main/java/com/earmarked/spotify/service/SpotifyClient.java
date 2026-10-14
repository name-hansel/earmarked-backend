package com.earmarked.spotify.service;

import com.earmarked.spotify.dto.spotify.SpotifySearchResponse;
import com.earmarked.spotify.dto.spotify.album.SpotifyAlbumFull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpotifyClient {
    private final RestClient spotifyRestClient;

    public SpotifySearchResponse searchAlbums(String query) {
        log.info("Calling Spotify: search albums '{}'", query);
        return spotifyRestClient
                .get()
                .uri(uri -> uri
                        .path("/search")
                        .queryParam("q", query)
                        .queryParam("type", "album")
                        .queryParam("limit", 10)
                        .build())
                .retrieve()
                .body(SpotifySearchResponse.class);
    }

    public SpotifyAlbumFull getAlbum(String id) {
        log.info("Calling Spotify: GET /albums/{}", id);
        return spotifyRestClient.get()
                .uri("/albums/{id}", id)
                .retrieve()
                .body(SpotifyAlbumFull.class);
    }
}
