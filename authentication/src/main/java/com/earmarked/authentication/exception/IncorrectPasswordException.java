package com.earmarked.authentication.exception;

import org.springframework.http.HttpStatus;

public class IncorrectPasswordException extends ApiException {
    public IncorrectPasswordException() {
        super("INCORRECT_PASSWORD", "Incorrect password entered.", HttpStatus.BAD_REQUEST);
    }
}
