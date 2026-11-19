package com.apptivedev.sns.domain.auth.interceptor;

import com.apptivedev.sns.domain.auth.exception.UnauthorizedException;
import com.apptivedev.sns.domain.auth.jwt.JwtProvider;
import com.apptivedev.sns.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    public static final String LOGIN_USER_ID = "loginUserId";

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 게시글 API는 이번 과제에서 생성 요청만 인증한다.
        if ("/posts".equals(request.getServletPath())
                && !"POST".equals(request.getMethod())) {
            return true;
        }

        String authorization = request.getHeader("Authorization");

        if (authorization == null
                || !authorization.matches("(?i)^Bearer [^\\s]+$")) {
            throw new UnauthorizedException(
                    "Bearer 인증 토큰이 필요합니다."
            );
        }

        String token = authorization.substring(7);
        Long userId = jwtProvider.getUserId(token);

        if (!userRepository.existsById(userId)) {
            throw new UnauthorizedException(
                    "사용할 수 없는 인증 정보입니다."
            );
        }

        request.setAttribute(LOGIN_USER_ID, userId);

        return true;
    }
}