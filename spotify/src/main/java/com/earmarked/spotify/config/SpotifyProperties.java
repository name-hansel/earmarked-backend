package com.earmarked.spotify.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spotify")
public record SpotifyProperties(String clientId, String clientSecret, String authUrl, String apiUrl) {
}
