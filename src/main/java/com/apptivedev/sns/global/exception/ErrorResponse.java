package com.apptivedev.sns.global.exception;

public record ErrorResponse(
        String code,
        String message
) {
}