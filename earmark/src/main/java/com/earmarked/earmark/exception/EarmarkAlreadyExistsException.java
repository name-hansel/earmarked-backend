package com.earmarked.earmark.exception;

import org.springframework.http.HttpStatus;

public class EarmarkAlreadyExistsException extends ApiException {

    public EarmarkAlreadyExistsException() {
        super("EARMARK_ALREADY_EXISTS", "Earmark already exists", HttpStatus.CONFLICT);
    }
}
