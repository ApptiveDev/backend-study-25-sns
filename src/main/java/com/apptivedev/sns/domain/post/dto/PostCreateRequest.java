package com.apptivedev.sns.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostCreateRequest(
        @Schema(example = "첫 게시글", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 100)
        String title,
        @Schema(example = "게시글 내용입니다.", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 500)
        String content) {
}
