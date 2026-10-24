package com.earmarked.earmark.exception;

import org.springframework.http.HttpStatus;

public class InvalidListenedDateException extends ApiException {

    public InvalidListenedDateException() {
        super("INVALID_LISTENED_DATE", "Invalid listened to date", HttpStatus.BAD_REQUEST);
    }
}
