package com.earmarked.authentication.config;

import com.earmarked.authentication.dto.ApiError;
import com.earmarked.authentication.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class AuthenticationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException e) {
        return ResponseEntity.status(e.getStatus()).body(new ApiError(e.getCode(), e.getMessage(), Map.of()));
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream().collect(
                Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage,
                        (existing, replacement) -> existing));

        return ResponseEntity.badRequest().body(new ApiError("VALIDATION_ERROR", null, fieldErrors));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        return ResponseEntity.badRequest()
                .body(new ApiError("INVALID_REQUEST", "Request body is missing or malformed", Map.of()));
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

        return ResponseEntity.status(statusCode).headers(headers).body(new ApiError(code, message, Map.of()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(Exception exception) {
        log.error("Unexpected error occurred", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("INTERNAL_SERVER_ERROR", "An unexpected error occurred",
                        Map.of()));
    }
}
