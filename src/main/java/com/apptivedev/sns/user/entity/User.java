package com.apptivedev.sns.user.entity;

import com.apptivedev.sns.global.exception.DomainException;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 3주차: DB에서도 이메일 중복 가입을 방지
    @Column(nullable = false, unique = true)
    private String email;

    // 3주차: 평문이 아닌 BCrypt Hash를 저장
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String name;

    // JPA가 사용하는 기본 생성자
    protected User() {
    }

    private User(String email, String passwordHash, String name) {

        if (email == null || email.isBlank()) {
            throw new DomainException("이메일은 필수입니다.");
        }

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new DomainException("비밀번호는 필수입니다.");
        }

        validateName(name);

        this.email = email;
        this.password = passwordHash;
        this.name = name;
    }

    // 정적 팩터리 메서드로 객체 생성 의도를 명확히 표현
    public static User create(
            String email, String passwordHash, String name) {

        return new User(email, passwordHash, name);
    }

    // 4주차: Setter 대신 의미 있는 메서드로 상태 변경
    public void changeName(String name) {
        validateName(name);
        this.name = name;
    }

    // 4주차: Entity가 자신의 상태에 대한 규칙을 직접 관리
    private static void validateName(String name) {

        if (name == null || name.isBlank() || name.length() > 20) {
            throw new DomainException("이름은 1~20자여야 합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}
