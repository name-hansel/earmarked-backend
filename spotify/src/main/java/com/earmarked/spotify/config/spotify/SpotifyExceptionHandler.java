package com.earmarked.spotify.config.spotify;

import com.earmarked.spotify.dto.ApiError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice
@Slf4j
public class SpotifyExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleValidation(ConstraintViolationException ex) {
        var message = ex.getConstraintViolations().stream()
                .findFirst().map(ConstraintViolation::getMessage).orElse("Invalid request parameters");
        return ResponseEntity.badRequest().body(new ApiError("VALIDATION_ERROR", message));
    }

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<ApiError> handleNotFound(HttpClientErrorException.NotFound e) {
        return ResponseEntity.badRequest().body(new ApiError("ALBUM_NOT_FOUND", "Album not found"));
    }

    @ExceptionHandler(HttpClientErrorException.TooManyRequests.class)
    public ResponseEntity<ApiError> handleRateLimit(HttpClientErrorException.TooManyRequests ex) {
        var retryAfter = ex.getResponseHeaders() == null ? null
                : ex.getResponseHeaders().getFirst(HttpHeaders.RETRY_AFTER);
        var response = ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS);

        if (retryAfter != null) response.header(HttpHeaders.RETRY_AFTER, retryAfter);

        return response.body(new ApiError("RATE_LIMITED", "Too many requests, try again later"));
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiError> handleSpotifyFailure(RestClientException ex) {
        log.error("Spotify call failed", ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ApiError("SPOTIFY_UNAVAILABLE", "Spotify is unavailable"));
    }
}
