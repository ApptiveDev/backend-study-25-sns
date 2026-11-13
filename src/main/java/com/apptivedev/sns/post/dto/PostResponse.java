package com.apptivedev.sns.post.dto;

public record PostResponse(
        Long id,
        String title,
        String content
) {
}
