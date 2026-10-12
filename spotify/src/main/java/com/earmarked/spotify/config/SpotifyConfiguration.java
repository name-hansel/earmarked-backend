package com.earmarked.spotify.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(SpotifyProperties.class)
public class SpotifyConfiguration {

    @Bean
    RestClient spotifyRestClient(RestClient.Builder builder, SpotifyProperties spotifyProperties, SpotifyTokenProvider spotifyTokenProvider) {
        return builder
                .baseUrl(spotifyProperties.apiUrl())
                .requestInterceptor((req, body, exec) -> {
                    req.getHeaders().setBearerAuth(spotifyTokenProvider.getToken());
                    return exec.execute(req, body);
                })
                .build();
    }
}
