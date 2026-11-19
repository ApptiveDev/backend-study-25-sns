package com.apptivedev.sns.domain.post.dto;

import jakarta.validation.constraints.Size;

// 게시글 수정 요청 양식 (보낸 필드만 수정, 안 보낸 필드는 null로 들어옴)
public record PostUpdateRequest(

        @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
        String title,

        @Size(max = 500, message = "내용은 500자 이하여야 합니다.")
        String content
) {
}