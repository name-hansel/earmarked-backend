package com.earmarked.authentication.exception;

import org.springframework.http.HttpStatus;

public class InvalidAccountActivationTokenException extends ApiException {

    public InvalidAccountActivationTokenException() {
        super("INVALID_TOKEN", "Invalid account activation link", HttpStatus.BAD_REQUEST);
    }
}
