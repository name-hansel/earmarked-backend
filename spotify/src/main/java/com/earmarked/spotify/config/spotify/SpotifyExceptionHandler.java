package com.earmarked.spotify.config.spotify;

import com.earmarked.spotify.dto.ApiError;
import feign.FeignException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class SpotifyExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        ApiError body = new ApiError(
                "INVALID_PARAMETER",
                "Invalid value for '" + e.getName() + "'");
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleValidation(ConstraintViolationException ex) {
        var message = ex.getConstraintViolations().stream()
                .findFirst().map(ConstraintViolation::getMessage).orElse("Invalid request parameters");
        return ResponseEntity.badRequest().body(new ApiError("VALIDATION_ERROR", message));
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.badRequest()
                .body(new ApiError("INVALID_REQUEST", "Missing required parameter: " + ex.getParameterName()));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        return ResponseEntity.badRequest().body(new ApiError("INVALID_REQUEST", "Invalid request parameters"));
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex,
                                                                            HttpHeaders headers, HttpStatusCode status,
                                                                            WebRequest request) {
        String message = ex.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("Invalid request parameters");

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

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, @Nullable Object body,
                                                             HttpHeaders headers, HttpStatusCode statusCode,
                                                             WebRequest request) {
        if (statusCode.is5xxServerError()) {
            log.error("Server error while handling request", exception);
        }

        HttpStatus status = HttpStatus.resolve(statusCode.value());
        String code = status != null ? status.name() : "ERROR";
        String message = status != null ? status.getReasonPhrase() : "Request failed";

        return ResponseEntity.status(statusCode).headers(headers).body(new ApiError(code, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(Exception exception) {
        log.error("Unexpected error occurred", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
    }

}
