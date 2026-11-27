package com.apptivedev.sns.domain.post.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record PostPageResponse(
        List<PostResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {
    public static PostPageResponse from(Page<PostResponse> posts) {
        return new PostPageResponse(
                posts.getContent(),
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalElements(),
                posts.getTotalPages(),
                posts.hasNext()
        );
    }
}
