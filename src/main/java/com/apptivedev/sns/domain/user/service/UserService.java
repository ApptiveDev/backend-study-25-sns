package com.apptivedev.sns.domain.user.service;

import com.apptivedev.sns.domain.auth.exception.ForbiddenException;
import com.apptivedev.sns.domain.auth.exception.UnauthorizedException;
import com.apptivedev.sns.domain.post.repository.PostRepository;
import com.apptivedev.sns.domain.user.dto.UpdateUserRequest;
import com.apptivedev.sns.domain.user.dto.UserResponse;
import com.apptivedev.sns.domain.user.entity.User;
import com.apptivedev.sns.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public UserResponse getUser(Long loginUserId, Long userId) {
        User user = findOwnUser(loginUserId, userId);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateUser(
            Long loginUserId,
            Long userId,
            UpdateUserRequest request
    ) {
        User user = findOwnUser(loginUserId, userId);

        user.changeName(request.name());

        return UserResponse.from(user);
    }

    @Transactional
    public void deleteUser(Long loginUserId, Long userId) {
        User user = findOwnUser(loginUserId, userId);

        postRepository.deleteAllByAuthor_Id(userId);
        postRepository.flush();

        userRepository.delete(user);
    }

    private User findOwnUser(Long loginUserId, Long userId) {
        if (!loginUserId.equals(userId)) {
            throw new ForbiddenException();
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException(
                        "사용할 수 없는 인증 정보입니다."
                ));
    }
}