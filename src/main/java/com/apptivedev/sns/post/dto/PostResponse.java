package com.apptivedev.sns.post.dto;

import com.apptivedev.sns.post.entity.Post;

public record PostResponse(Long id, String title, String content) {

    public static PostResponse from(Post post) {
        return new PostResponse(post.getId(), post.getTitle(), post.getContent());
    }
}