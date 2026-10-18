package com.earmarked.earmark.exception.album;

import com.earmarked.earmark.exception.ApiException;
import org.springframework.http.HttpStatus;

public class AlbumNotFoundException extends ApiException {

    public AlbumNotFoundException() {
        super("ALBUM_NOT_FOUND", "Album not found", HttpStatus.NOT_FOUND);
    }
}
