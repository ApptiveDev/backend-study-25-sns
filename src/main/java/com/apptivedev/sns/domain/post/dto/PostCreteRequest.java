package com.apptivedev.sns.domain.post.dto;

// 게시글 생성 요청 양식 (id는 서버가 만들기 때문에 받지 않는다) -> 중요!
public record PostCreateRequest(
        String title,
        String content
) {
}