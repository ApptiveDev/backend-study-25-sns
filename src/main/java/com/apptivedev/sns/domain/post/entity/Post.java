package com.apptivedev.sns.domain.post.entity;

import com.apptivedev.sns.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Post(User author, String title, String content) {
        if (author == null) {
            throw new IllegalArgumentException("게시글 작성자가 필요합니다.");
        }

        validateTitle(title);
        validateContent(content);

        this.author = author;
        this.title = title;
        this.content = content;
    }

    public void changeTitle(String title) {
        validateTitle(title);
        this.title = title;
    }

    public void changeContent(String content) {
        validateContent(content);
        this.content = content;
    }

    public void changeText(String title, String content) {
        // 두 값이 모두 유효한지 확인한 다음 상태를 바꾼다.
        validateTitle(title);
        validateContent(content);
        this.title = title;
        this.content = content;
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank() || title.length() > 100) {
            throw new IllegalArgumentException("제목은 비어 있을 수 없고 100자 이하여야 합니다.");
        }
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank() || content.length() > 500) {
            throw new IllegalArgumentException("내용은 비어 있을 수 없고 500자 이하여야 합니다.");
        }
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
