package com.earmarked.authentication.exception;

import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends ApiException {

    public InvalidRefreshTokenException() {
        super("INVALID_REFRESH_TOKEN", "Unauthorized", HttpStatus.UNAUTHORIZED);
    }
}
