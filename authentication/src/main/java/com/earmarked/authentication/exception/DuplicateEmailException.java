package com.earmarked.authentication.exception;

import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends ApiException {

    public DuplicateEmailException() {
        super("DUPLICATE_EMAIL", "Account already exists.", HttpStatus.BAD_REQUEST);
    }
}
