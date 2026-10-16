package com.earmarked.spotify.config.spotify;

import com.earmarked.spotify.dto.spotify.auth.SpotifyTokenResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Component
@EnableConfigurationProperties(SpotifyProperties.class)
public class SpotifyTokenProvider {
    private final SpotifyAuthApi spotifyAuthApi;
    private final SpotifyProperties spotifyProperties;
    private Instant expiresAt = Instant.EPOCH;
    private String token;

    public SpotifyTokenProvider(SpotifyAuthApi spotifyAuthApi, SpotifyProperties spotifyProperties) {
        this.spotifyAuthApi = spotifyAuthApi;
        this.spotifyProperties = spotifyProperties;
    }

    public synchronized String getToken() {
        if (Instant.now().isAfter(expiresAt.minusSeconds(30))) {
            refresh();
        }

        return token;
    }

    private void refresh() {
        String credentials = spotifyProperties.clientId() + ":" + spotifyProperties.clientSecret();
        String basicAuth = "Basic " + Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");

        SpotifyTokenResponse spotifyTokenResponse = spotifyAuthApi.requestToken(basicAuth, form);

        assert spotifyTokenResponse != null;
        token = spotifyTokenResponse.accessToken();
        expiresAt = Instant.now().plusSeconds(spotifyTokenResponse.expiresIn());
    }
}
