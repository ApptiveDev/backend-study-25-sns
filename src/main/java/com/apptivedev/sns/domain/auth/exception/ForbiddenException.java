package com.apptivedev.sns.domain.auth.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {
        super("본인의 정보에만 접근할 수 있습니다.");
    }
}