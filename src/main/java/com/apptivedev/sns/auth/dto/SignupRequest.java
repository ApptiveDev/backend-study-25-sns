package com.apptivedev.sns.auth.dto;

import jakarta.validation.constraints.*;

public record SignupRequest(

        // 3주차: Validation으로 잘못된 HTTP 입력을 차단
        @NotBlank
        @Email
        @Size(max = 254)
        String email,

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[^A-Za-z0-9])[!-~]{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8~20자여야 합니다."
        )
        String password,

        @NotBlank
        @Size(max = 20)
        String name
) {
}