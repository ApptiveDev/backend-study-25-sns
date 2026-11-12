package com.apptivedev.sns.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException e) {

        ErrorResponse response = new ErrorResponse(
                e.getStatusCode().value(),
                e.getReason()
        );

        return ResponseEntity
                .status(e.getStatusCode())
                .body(response);
    }

    public record ErrorResponse(
            int status,
            String message
    ) {
    }
}
