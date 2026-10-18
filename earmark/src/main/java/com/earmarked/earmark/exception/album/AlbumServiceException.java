package com.earmarked.earmark.exception.album;

import com.earmarked.earmark.exception.ApiException;
import org.springframework.http.HttpStatus;

public class AlbumServiceException extends ApiException {

    public AlbumServiceException(HttpStatus code) {
        super("ALBUM_SERVICE_EXCEPTION", "Something went wrong.", code);
    }
}
