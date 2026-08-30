package com.earmarked.authentication.exception;

import org.springframework.http.HttpStatus;

public class InvalidAccountActivationTokenException extends ApiException {

    public InvalidAccountActivationTokenException() {
        super("INVALID_ACCOUNT_ACTIVATION_LINK", "Invalid account activation link", HttpStatus.BAD_REQUEST);
    }
}
