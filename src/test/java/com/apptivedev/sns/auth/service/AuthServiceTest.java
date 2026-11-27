package com.apptivedev.sns.auth.service;

import com.apptivedev.sns.auth.dto.SignupRequest;
import com.apptivedev.sns.auth.jwt.JwtProvider;
import com.apptivedev.sns.global.exception.BusinessException;
import com.apptivedev.sns.user.entity.User;
import com.apptivedev.sns.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtProvider jwtProvider;
    @InjectMocks AuthService authService;
    private final SignupRequest request = new SignupRequest("test@example.com", "Study1234!", "테스터");
    @Test
    void signup_success() {
        given(userRepository.existsByEmail(request.email())).willReturn(false);
        given(passwordEncoder.encode(request.password())).willReturn("test-hash");
        given(userRepository.saveAndFlush(any(User.class))).willAnswer(i -> i.getArgument(0));
        assertThat(authService.signup(request).name()).isEqualTo("테스터");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("test-hash");
    }
    @Test
    void signup_duplicateEmail_doesNotSave() {
        given(userRepository.existsByEmail(request.email())).willReturn(true);
        assertThatThrownBy(() -> authService.signup(request)).isInstanceOf(BusinessException.class);
        verify(userRepository, never()).saveAndFlush(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }
}
