package com.apptivedev.sns.auth.service;

import com.apptivedev.sns.auth.dto.SignupRequest;
import com.apptivedev.sns.auth.dto.LoginRequest;
import com.apptivedev.sns.auth.dto.LoginResponse;
import com.apptivedev.sns.auth.jwt.JwtProvider;
import com.apptivedev.sns.global.exception.BusinessException;
import com.apptivedev.sns.user.dto.UserResponse;
import com.apptivedev.sns.user.entity.User;
import com.apptivedev.sns.user.repository.UserRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 1주차: 생성자 주입을 이용한 DI
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public UserResponse signup(SignupRequest request) {

        // 3주차: 이메일 중복 가입 방지
        if (userRepository.existsByEmail(request.email())) {
            throw BusinessException.duplicateEmail();
        }

        // 3주차: 비밀번호를 평문으로 저장하지 않고 Hash 처리
        String encodedPassword =
                passwordEncoder.encode(request.password());

        User user = User.create(
                request.email(),
                encodedPassword,
                request.name()
        );

        try {
            // DB의 UNIQUE 제약조건으로 동시 중복 가입도 방지
            User savedUser = userRepository.saveAndFlush(user);

            return UserResponse.from(savedUser);

        } catch (DataIntegrityViolationException e) {
            throw BusinessException.duplicateEmail();
        }
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(BusinessException::unauthorized);

        // BCrypt 입력 길이 제한 확인
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw BusinessException.unauthorized();
        }

        // 3주차: BCrypt는 Salt를 사용하므로 encode 결과를
        // 직접 비교하지 않고 matches()로 비밀번호 검증
        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {
            throw BusinessException.unauthorized();
        }

        String token = jwtProvider.createToken(user.getId());

        return new LoginResponse(token);
    }
}
