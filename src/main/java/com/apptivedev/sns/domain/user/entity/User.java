package com.apptivedev.sns.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "nickname", nullable = false, length = 20)
    private String name;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private User(String email, String password, String name) {
        validateEmail(email);
        validatePasswordHash(password);
        validateName(name);

        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static User create(
            String email,
            String passwordHash,
            String name
    ) {
        return new User(email, passwordHash, name);
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank() || email.length() > 100) {
            throw new IllegalArgumentException(
                    "이메일은 비어 있을 수 없고 100자 이하여야 합니다."
            );
        }
    }

    private void validatePasswordHash(String passwordHash) {
        if (passwordHash == null
                || passwordHash.isBlank()
                || passwordHash.length() > 255) {
            throw new IllegalArgumentException(
                    "저장할 비밀번호 Hash가 올바르지 않습니다."
            );
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank() || name.length() > 20) {
            throw new IllegalArgumentException(
                    "이름은 비어 있을 수 없고 20자 이하여야 합니다."
            );
        }
    }

    public void changeName(String name) {
        validateName(name);
        this.name = name;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}