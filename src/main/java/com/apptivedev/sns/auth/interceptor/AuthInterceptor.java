package com.apptivedev.sns.auth.interceptor;

import com.apptivedev.sns.auth.jwt.JwtProvider;
import com.apptivedev.sns.global.exception.BusinessException;
import com.apptivedev.sns.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String USER_ID = "loginUserId";

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public AuthInterceptor(
            JwtProvider jwtProvider,
            UserRepository userRepository
    ) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    // 3주차: Controller가 실행되기 전에 공통 인증 처리
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {

        // 기존 게시글 조회·수정·삭제는 이번 과제의 인증 필수 범위에서 제외
        String path = request.getRequestURI()
                .substring(request.getContextPath().length());

        if (path.equals("/posts")
                && !"POST".equals(request.getMethod())) {
            return true;
        }

        String authorization =
                request.getHeader("Authorization");

        if (authorization == null
                || !authorization.regionMatches(
                true, 0, "Bearer ", 0, 7)) {
            throw BusinessException.unauthorized();
        }

        String token = authorization.substring(7);

        Long userId = jwtProvider.validateToken(token);

        // 탈퇴한 사용자가 기존 JWT를 사용하는 경우도 인증 실패
        if (!userRepository.existsById(userId)) {
            throw BusinessException.unauthorized();
        }

        // 인증된 사용자 ID를 현재 요청 범위에 저장
        // 공유 필드에 저장하지 않아 다른 요청과 섞이지 않도록 함
        request.setAttribute(USER_ID, userId);

        return true;
    }
}
