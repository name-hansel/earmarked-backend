package com.earmarked.spotify.service;

import com.earmarked.spotify.dto.spotify.SpotifyAlbum;
import com.earmarked.spotify.dto.spotify.SpotifySearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class SpotifyClient {
    private final RestClient spotifyRestClient;

    public SpotifySearchResponse searchAlbums(String query) {
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

    public SpotifyAlbum getAlbum(String id) {
        return spotifyRestClient.get()
                .uri("/albums/{id}", id)
                .retrieve()
                .body(SpotifyAlbum.class);
    }
}
