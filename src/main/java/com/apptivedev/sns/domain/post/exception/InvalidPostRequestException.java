package com.apptivedev.sns.domain.post.exception;

public class InvalidPostRequestException extends RuntimeException {
    public InvalidPostRequestException(String message) {
        super(message);
    }
}
