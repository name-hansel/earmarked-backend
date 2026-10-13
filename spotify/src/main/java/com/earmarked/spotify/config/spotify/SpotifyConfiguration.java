package com.earmarked.spotify.config.spotify;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(SpotifyProperties.class)
public class SpotifyConfiguration {

    @Bean
    RestClient spotifyRestClient(SpotifyProperties spotifyProperties, SpotifyTokenProvider spotifyTokenProvider) {
        return RestClient.builder()
                .baseUrl(spotifyProperties.apiUrl())
                .requestFactory(SpotifyHttp.requestFactory())
                .requestInterceptor((req, body, exec) -> {
                    req.getHeaders().setBearerAuth(spotifyTokenProvider.getToken());
                    return exec.execute(req, body);
                })
                .build();
    }
}
