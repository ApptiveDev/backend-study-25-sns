package com.apptivedev.sns.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
        @NotBlank(message = "제목은 비어 있을 수 없습니다.")
        @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
        String title,

        @NotBlank(message = "내용은 비어 있을 수 없습니다.")
        @Size(max = 500, message = "내용은 500자 이하여야 합니다.")
        String content
) {
}
