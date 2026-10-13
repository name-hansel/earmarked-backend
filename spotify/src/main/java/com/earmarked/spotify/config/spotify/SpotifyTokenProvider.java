package com.earmarked.spotify.config.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Instant;

@Component
public class SpotifyTokenProvider {
    private final RestClient restClient;
    private final SpotifyProperties spotifyProperties;
    private Instant expiresAt = Instant.EPOCH;
    private String token;

    public SpotifyTokenProvider(SpotifyProperties spotifyProperties) {
        this.spotifyProperties = spotifyProperties;
        this.restClient = RestClient.builder().baseUrl(spotifyProperties.authUrl())
                .requestFactory(SpotifyHttp.requestFactory()).build();
    }

    public synchronized String getToken() {
        if (Instant.now().isAfter(expiresAt.minusSeconds(30))) {
            refresh();
        }

        return token;
    }

    private void refresh() {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "client_credentials");

        TokenResponse tokenResponse = restClient.post().uri("/api/token")
                .headers(h -> h.setBasicAuth(spotifyProperties.clientId(), spotifyProperties.clientSecret()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(TokenResponse.class);

        assert tokenResponse != null;
        token = tokenResponse.accessToken();
        expiresAt = Instant.now().plusSeconds(tokenResponse.expiresIn());
    }

    private record TokenResponse(@JsonProperty("access_token") String accessToken,
                                 @JsonProperty("expires_in") long expiresIn) {
    }
}
