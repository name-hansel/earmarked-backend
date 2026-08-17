package com.earmarked.authentication.exception;

import org.springframework.http.HttpStatus;

public class InvalidLoginException extends ApiException {

    public InvalidLoginException() {
        super("UNABLE_TO_LOGIN", "Unable to log in. Please check your credentials or activate your account.",
                HttpStatus.FORBIDDEN);
    }
}
