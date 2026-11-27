package com.apptivedev.sns.user.repository;

import com.apptivedev.sns.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 3주차: 회원가입 시 이메일 중복 확인
    boolean existsByEmail(String email);

    // 3주차: 로그인 시 이메일로 사용자 조회
    Optional<User> findByEmail(String email);
}
