package com.apptivedev.sns.post.dto;

public record PostCreateRequest(
        String title,
        String content
) {
}