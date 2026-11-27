package com.apptivedev.sns.user.service;

import com.apptivedev.sns.global.exception.BusinessException;
import com.apptivedev.sns.post.repository.PostRepository;
import com.apptivedev.sns.user.dto.*;
import com.apptivedev.sns.user.entity.User;
import com.apptivedev.sns.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    public UserService(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }
    // 3주차: 상대 사용자 존재 여부를 조회하기 전에 인가를 확인한다.
    private User ownUser(Long loginId, Long userId) {
        if (!loginId.equals(userId)) throw BusinessException.forbidden();
        return userRepository.findById(userId).orElseThrow(BusinessException::unauthorized);
    }
    @Transactional(readOnly = true)
    public UserResponse getUser(Long loginId, Long userId) { return UserResponse.from(ownUser(loginId, userId)); }
    @Transactional
    public UserResponse updateUser(Long loginId, Long userId, UserUpdateRequest request) {
        User user = ownUser(loginId, userId);
        user.changeName(request.name()); // 4주차: 상태 변경은 Entity의 책임
        return UserResponse.from(user);
    }
    @Transactional
    public void deleteUser(Long loginId, Long userId) {
        User user = ownUser(loginId, userId);
        // 탈퇴 정책: 작성 게시글을 먼저 삭제하여 FK 오류를 방지한다.
        postRepository.deleteAllByUserId(userId);
        postRepository.flush();
        userRepository.delete(user);
    }
}
