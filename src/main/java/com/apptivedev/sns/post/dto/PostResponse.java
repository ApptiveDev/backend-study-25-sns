package com.apptivedev.sns.post.dto;

import com.apptivedev.sns.post.entity.Post;

// 3주차: 작성자 식별자와 이름만 응답으로 제공한다.
public record PostResponse(Long id, String title, String content, Long authorId, String authorName) {
    public static PostResponse from(Post post) {
        return new PostResponse(post.getId(), post.getTitle(), post.getContent(),
                post.getUser().getId(), post.getUser().getName());
    }
}
