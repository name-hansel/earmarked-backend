package com.earmarked.earmark.exception.album;

import com.earmarked.earmark.exception.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidAlbumIdException extends ApiException {

    public InvalidAlbumIdException() {
        super("INVALID_ALBUM_ID", "Invalid album ID", HttpStatus.BAD_REQUEST);
    }
}
