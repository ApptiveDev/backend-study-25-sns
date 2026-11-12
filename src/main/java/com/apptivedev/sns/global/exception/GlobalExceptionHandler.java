package com.apptivedev.sns.global.exception;

import com.apptivedev.sns.domain.post.exception.InvalidPostRequestException;
import com.apptivedev.sns.domain.post.exception.PostNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(InvalidPostRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPost(InvalidPostRequestException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFound(PostNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception, Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        String message = switch (status.value()) {
            case 400 -> "요청 형식이 올바르지 않습니다. JSON 본문과 게시글 ID를 확인해 주세요.";
            case 404 -> "요청한 리소스를 찾을 수 없습니다.";
            case 405 -> "지원하지 않는 HTTP 메서드입니다.";
            case 415 -> "Content-Type을 application/json으로 보내 주세요.";
            default -> "요청을 처리할 수 없습니다.";
        };
        return super.handleExceptionInternal(exception, new ErrorResponse(message), headers, status, request);
    }
}
