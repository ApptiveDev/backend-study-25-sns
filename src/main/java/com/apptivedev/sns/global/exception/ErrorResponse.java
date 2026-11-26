package com.apptivedev.sns.global.exception;

// 3주차: 오류 응답 형식을 통일
public record ErrorResponse(
        String code,
        String message
) {
}
