package com.earmarked.earmark.exception;

import org.springframework.http.HttpStatus;

public class EarmarkNotFoundException extends ApiException {

    public EarmarkNotFoundException() {
        super("EARMARK_NOT_FOUND", "Earmark not found", HttpStatus.NOT_FOUND);
    }
}
