package com.apptivedev.sns.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 게시글 생성 요청 양식 (id는 서버가 만들기 때문에 받지 않는다) -> 중요!
public record PostCreateRequest(

        @NotBlank(message = "제목은 필수이며 공백일 수 없습니다.")
        @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
        String title,

        @NotBlank(message = "내용은 필수이며 공백일 수 없습니다.")
        @Size(max = 500, message = "내용은 500자 이하여야 합니다.")
        String content
) {
}