package com.apptivedev.sns.domain.post.dto;

public record PostResponse(
        Long id,
        String title,
        String content
) {
}