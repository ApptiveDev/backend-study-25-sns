package com.apptivedev.sns.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostCreateRequest(
        @NotBlank(message = "제목을 입력해 주세요.")
        @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
        String title,

        @NotBlank(message = "본문을 입력해 주세요.")
        @Size(max = 500, message = "본문은 500자 이하여야 합니다.")
        String content
) {
}