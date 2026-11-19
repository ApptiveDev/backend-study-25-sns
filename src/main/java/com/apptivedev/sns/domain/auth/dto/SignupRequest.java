package com.apptivedev.sns.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(
                min = 8,
                max = 20,
                message = "비밀번호는 8~20자여야 합니다."
        )
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[^A-Za-z0-9])[!-~]+$",
                message = "비밀번호는 영문, 숫자, ASCII 특수문자를 포함하고 공백 없이 입력해주세요."
        )
        String password,

        @NotBlank(message = "이름을 입력해주세요.")
        @Size(max = 20, message = "이름은 20자 이하여야 합니다.")
        String name
) {
}