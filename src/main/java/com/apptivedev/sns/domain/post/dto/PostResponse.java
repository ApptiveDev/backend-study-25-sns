package com.apptivedev.sns.domain.post.dto;

import com.apptivedev.sns.domain.post.entity.Post;
import com.apptivedev.sns.domain.user.entity.User;

public record PostResponse(
        Long id,
        String title,
        String content,
        Long authorId,
        String authorName
) {

    public static PostResponse from(Post post) {
        User author = post.getAuthor();

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                author.getId(),
                author.getName()
        );
    }
}