package com.apptivedev.sns.post.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// PATCH에서 null은 기존 값 유지, 값이 있으면 공백과 길이를 검사한다.
public record PostUpdateRequest(
        @Size(max = 100) @Pattern(regexp = "(?s).*[^\\p{javaWhitespace}].*") String title,
        @Size(max = 500) @Pattern(regexp = "(?s).*[^\\p{javaWhitespace}].*") String content) {}
