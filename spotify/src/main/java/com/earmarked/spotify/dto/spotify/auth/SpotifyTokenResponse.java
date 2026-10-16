package com.earmarked.spotify.dto.spotify.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyTokenResponse(@JsonProperty("access_token") String accessToken,
                                   @JsonProperty("expires_in") long expiresIn) {
}
