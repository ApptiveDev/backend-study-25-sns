package com.apptivedev.sns.auth.controller;

import com.apptivedev.sns.auth.dto.SignupRequest;
import com.apptivedev.sns.auth.dto.LoginRequest;
import com.apptivedev.sns.auth.dto.LoginResponse;
import com.apptivedev.sns.auth.service.AuthService;
import com.apptivedev.sns.user.dto.UserResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 3주차: @Valid로 DTO 입력값 검증
    // Controller는 요청과 응답을 담당하고 로직은 Service에 위임
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse signup(
            @Valid @RequestBody SignupRequest request
    ) {
        return authService.signup(request);
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }
}
