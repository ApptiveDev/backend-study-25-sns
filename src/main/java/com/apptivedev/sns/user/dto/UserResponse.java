package com.apptivedev.sns.user.dto;

import com.apptivedev.sns.user.entity.User;

// 3주차: Entity를 직접 반환하지 않고 비밀번호 노출 방지
public record UserResponse(
        Long id,
        String email,
        String name
) {

    // 4주차: 정적 팩터리 메서드를 활용한 DTO 변환
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }
}