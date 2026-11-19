package com.apptivedev.sns.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(

        @NotBlank(message = "이름을 입력해주세요.")
        @Size(max = 20, message = "이름은 20자 이하여야 합니다.")
        String name
) {
}