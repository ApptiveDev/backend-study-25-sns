package com.apptivedev.sns.global.exception;

import org.springframework.http.HttpStatus;

// 3주차: 상황에 따라 HTTP 상태 코드와 오류 메시지를 구분
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public BusinessException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public static BusinessException unauthorized() {
        return new BusinessException(
                HttpStatus.UNAUTHORIZED,
                "UNAUTHORIZED",
                "인증에 실패했습니다.");
    }

    public static BusinessException forbidden() {
        return new BusinessException(
                HttpStatus.FORBIDDEN,
                "FORBIDDEN",
                "접근 권한이 없습니다.");
    }

    public static BusinessException duplicateEmail() {
        return new BusinessException(
                HttpStatus.CONFLICT,
                "DUPLICATE_EMAIL",
                "이미 가입된 이메일입니다.");
    }

    public static BusinessException notFound() {
        return new BusinessException(
                HttpStatus.NOT_FOUND,
                "NOT_FOUND",
                "대상을 찾을 수 없습니다.");
    }
}
