package com.earmarked.authentication.exception;

import org.springframework.http.HttpStatus;

public class PasswordSameAsCurrentException extends ApiException {
    public PasswordSameAsCurrentException() {
        super("NEW_PASSWORD_SAME_AS_CURRENT_PASSWORD", "New password cannot be same as current password.",
                HttpStatus.BAD_REQUEST);
    }
}
