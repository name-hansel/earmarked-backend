package com.earmarked.authentication.exception;

import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends ApiException {

    public DuplicateEmailException(boolean verificationResent) {
        super("DUPLICATE_EMAIL",
                verificationResent ? "Account already exists. Please check your inbox for verification." : "Account already exists.",
                HttpStatus.BAD_REQUEST);
    }
}
