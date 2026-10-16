package com.earmarked.spotify.config.spotify;

import com.earmarked.spotify.dto.spotify.auth.SpotifyTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "spotify-auth-api", url = "${spotify.auth-url}")
public interface SpotifyAuthApi {

    @PostMapping(value = "/api/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    SpotifyTokenResponse requestToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String basicAuth, @RequestBody MultiValueMap<String, String> form);
}
