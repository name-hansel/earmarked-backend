package com.earmarked.authentication.exception;

import org.springframework.http.HttpStatus;

public class InvalidResetPasswordTokenException extends ApiException {

    public InvalidResetPasswordTokenException() {
        super("INVALID_RESET_PASSWORD_LINK", "Invalid reset password link", HttpStatus.BAD_REQUEST);
    }
}
