package com.apptivedev.sns.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 3주차: 요청 DTO의 1차 검증. 작성자는 요청 Body로 받지 않는다.
public record PostCreateRequest(@NotBlank @Size(max = 100) String title,
                                @NotBlank @Size(max = 500) String content) {}
