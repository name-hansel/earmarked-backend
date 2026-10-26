package com.earmarked.spotify.config.spotify;

import com.earmarked.spotify.dto.ApiError;
import feign.FeignException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class SpotifyExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleValidation(ConstraintViolationException ex) {
        var message = ex.getConstraintViolations().stream()
                .findFirst().map(ConstraintViolation::getMessage).orElse("Invalid request parameters");
        return ResponseEntity.badRequest().body(new ApiError("VALIDATION_ERROR", message));
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ApiError> handleNotFound(FeignException.NotFound e) {
        return ResponseEntity.badRequest().body(new ApiError("ALBUM_NOT_FOUND", "Album not found"));
    }

    @ExceptionHandler(FeignException.TooManyRequests.class)
    public ResponseEntity<ApiError> handleRateLimit(FeignException.TooManyRequests ex) {
        log.warn("Spotify rate limit hit");
        var response = ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE);

        ex.responseHeaders().getOrDefault(HttpHeaders.RETRY_AFTER, List.of()).stream()
                .findFirst()
                .ifPresent(retryAfter -> response.header(HttpHeaders.RETRY_AFTER, retryAfter));

        return response.body(new ApiError("RATE_LIMITED", "Too many requests, try again later"));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiError> handleSpotifyFailure(FeignException ex) {
        log.error("Spotify call failed", ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ApiError("SPOTIFY_UNAVAILABLE", "Spotify is unavailable"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        ApiError body = new ApiError(
                "INVALID_PARAMETER",
                "Invalid value for '" + e.getName() + "'");
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            HandlerMethodValidationException.class
    })
    public ResponseEntity<ApiError> handleInvalidRequest(Exception ex) {
        String message = switch (ex) {
            case MissingServletRequestParameterException e -> "Missing required parameter: " + e.getParameterName();
            default -> "Invalid request parameters";
        };

        return ResponseEntity.badRequest().body(new ApiError("INVALID_REQUEST", message));
    }
}
