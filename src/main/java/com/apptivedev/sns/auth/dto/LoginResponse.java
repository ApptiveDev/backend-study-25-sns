package com.apptivedev.sns.auth.dto;

// 3주차: 로그인 성공 시 Access Token 반환
public record LoginResponse(
        String accessToken
) {
}