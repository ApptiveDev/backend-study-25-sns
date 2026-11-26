package com.apptivedev.sns.global.exception;

// 4주차: Entity의 도메인 규칙 위반을 별도로 구분
public class DomainException extends IllegalArgumentException {

    public DomainException(String message) {
        super(message);
    }
}
