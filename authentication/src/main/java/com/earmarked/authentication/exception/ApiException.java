package com.earmarked.authentication.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private final String message;
    private final HttpStatus status;

    public ApiException(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
