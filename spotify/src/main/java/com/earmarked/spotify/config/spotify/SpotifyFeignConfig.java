package com.earmarked.spotify.config.spotify;

import com.earmarked.spotify.service.SpotifyTokenProvider;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

public class SpotifyFeignConfig {

    @Bean
    RequestInterceptor spotifyAuthInterceptor(SpotifyTokenProvider spotifyTokenProvider) {
        return template -> template.header(HttpHeaders.AUTHORIZATION, "Bearer " + spotifyTokenProvider.getToken());
    }

}
