package com.earmarked.earmark.exception.album;

import com.earmarked.earmark.exception.ApiException;
import org.springframework.http.HttpStatus;

public class AlbumServiceUnavailableException extends ApiException {

    public AlbumServiceUnavailableException() {
        super("ALBUM_SERVICE_UNAVAILABLE", "Spotify service is unavailable", HttpStatus.SERVICE_UNAVAILABLE);
    }
}
