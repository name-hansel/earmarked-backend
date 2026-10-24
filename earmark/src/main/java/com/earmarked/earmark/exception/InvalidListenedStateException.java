package com.earmarked.earmark.exception;

import org.springframework.http.HttpStatus;

public class InvalidListenedStateException extends ApiException {

    public InvalidListenedStateException() {
        super("INVALID_LISTENED_STATE", "Earmark has invalid listened state", HttpStatus.BAD_REQUEST);
    }
}
