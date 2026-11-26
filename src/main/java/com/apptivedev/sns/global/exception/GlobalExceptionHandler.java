package com.apptivedev.sns.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

// 3주차: Controller마다 예외 처리를 반복하지 않고 공통으로 처리
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(
            BusinessException e) {

        return ResponseEntity.status(e.getStatus())
                .body(new ErrorResponse(
                        e.getCode(), e.getMessage()));
    }

    // 4주차: Entity 내부 검증 실패를 400으로 반환
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomain(
            DomainException e) {

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        "INVALID_INPUT", e.getMessage()));
    }

    // 3주차: @Valid 검증 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException e) {

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        "INVALID_INPUT", "입력값을 확인해주세요."));
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErrorResponse> handleInvalidRequest(
            Exception e) {

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        "INVALID_INPUT", "요청 형식이 올바르지 않습니다."));
    }
}
