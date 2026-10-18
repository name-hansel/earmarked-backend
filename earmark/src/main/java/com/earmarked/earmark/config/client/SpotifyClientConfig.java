package com.earmarked.earmark.config.client;

import com.earmarked.earmark.exception.album.AlbumNotFoundException;
import com.earmarked.earmark.exception.album.AlbumServiceException;
import com.earmarked.earmark.exception.album.AlbumServiceUnavailableException;
import com.earmarked.earmark.exception.album.InvalidAlbumIdException;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

public class SpotifyClientConfig {

    @Bean
    public ErrorDecoder getErrorDecoder() {
        return (methodKey, response) -> switch (response.status()) {
            case 400 -> new InvalidAlbumIdException();
            case 404 -> new AlbumNotFoundException();
            case 429, 500, 502, 503, 504 -> new AlbumServiceUnavailableException();
            default -> new AlbumServiceException(HttpStatus.valueOf(response.status()));
        };
    }
}
