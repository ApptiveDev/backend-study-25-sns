package com.apptivedev.sns.domain.auth.service;

import com.apptivedev.sns.domain.auth.dto.SignupRequest;
import com.apptivedev.sns.domain.auth.exception.DuplicateEmailException;
import com.apptivedev.sns.domain.user.dto.UserResponse;
import com.apptivedev.sns.domain.user.entity.User;
import com.apptivedev.sns.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.apptivedev.sns.domain.auth.dto.LoginRequest;
import com.apptivedev.sns.domain.auth.dto.LoginResponse;
import com.apptivedev.sns.domain.auth.exception.UnauthorizedException;
import com.apptivedev.sns.domain.auth.jwt.JwtProvider;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public UserResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException();
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = User.create(
                request.email(),
                passwordHash,
                request.name()
        );

        User savedUser;

        try {
            savedUser = userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException exception) {
            if (userRepository.existsByEmail(request.email())) {
                throw new DuplicateEmailException();
            }

            throw exception;
        }

        return UserResponse.from(savedUser);


    }
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException(
                        "이메일 또는 비밀번호가 일치하지 않습니다."
                ));

        boolean passwordMatches = passwordEncoder.matches(
                request.password(),
                user.getPassword()
        );

        if (!passwordMatches) {
            throw new UnauthorizedException(
                    "이메일 또는 비밀번호가 일치하지 않습니다."
            );
        }

        String accessToken = jwtProvider.createAccessToken(user.getId());

        return new LoginResponse(accessToken);
    }
}