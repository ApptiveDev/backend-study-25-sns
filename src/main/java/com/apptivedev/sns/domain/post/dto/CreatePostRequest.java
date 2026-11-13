package com.apptivedev.sns.domain.post.dto;

public record CreatePostRequest(
        String title,
        String content
) {
}