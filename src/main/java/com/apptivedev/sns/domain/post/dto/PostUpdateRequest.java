package com.apptivedev.sns.domain.post.dto;

// 게시글 수정 요청 양식 (보낸 필드만 수정, 안 보낸 필드는 null로 들어옴)
public record PostUpdateRequest(
        String title,
        String content
) {
}