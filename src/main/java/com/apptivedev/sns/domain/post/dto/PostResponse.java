package com.apptivedev.sns.domain.post.dto;

import com.apptivedev.sns.domain.post.entity.Post;

public record PostResponse(
        Long id,
        String title,
        String content
) {

    // 엔티티 → 응답 DTO 변환
    public static PostResponse from(Post post) {
        return new PostResponse(post.getId(), post.getTitle(), post.getContent());
    }
}