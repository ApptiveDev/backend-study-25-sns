package com.apptivedev.sns.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.HttpStatus;
import java.util.Map;

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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    // 잘못된 JSON, 필수 요청 본문 누락 같은 예외 상황 발생.
    public ResponseEntity<?> handleNotReadable(
            HttpMessageNotReadableException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", 400,
                        "message", "잘못된 요청 본문입니다."
                ));
    }

    //@ExceptionHandler(처리할예외.class)
    //public ResponseEntity<?> 메서드이름(처리할예외 e) {
    //    return ResponseEntity
    //            .status(HTTP상태코드)
    //            .body(반환할데이터);
    //}

    public record ErrorResponse(
            int status,
            String message
    ) {
    }
}
