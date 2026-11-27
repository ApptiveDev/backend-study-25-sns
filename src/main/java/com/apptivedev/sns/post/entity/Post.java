package com.apptivedev.sns.post.entity;

import com.apptivedev.sns.global.exception.DomainException;
import com.apptivedev.sns.user.entity.User;
import jakarta.persistence.*;

@Entity
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String title;
    @Column(nullable = false, length = 500)
    private String content;
    // 3주차: Post가 연관관계의 주인으로 user_id FK를 관리한다.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    protected Post() {} // JPA 전용 기본 생성자

    private Post(String title, String content, User user) {
        validateTitle(title);
        validateContent(content);
        if (user == null) throw new DomainException("작성자는 필수입니다.");
        this.title = title;
        this.content = content;
        this.user = user;
    }
    // 4주차: static factory로 생성 의도를 표현하고 private 생성자로 규칙 우회를 제한한다.
    public static Post createPost(String title, String content, User user) {
        return new Post(title, content, user);
    }
    // 4주차: HTTP DTO를 거치지 않아도 도메인 불변식을 지킨다.
    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) throw new DomainException("제목은 필수입니다.");
        if (title.length() > 100) throw new DomainException("제목은 100자 이하여야 합니다.");
    }
    private static void validateContent(String content) {
        if (content == null || content.isBlank()) throw new DomainException("내용은 필수입니다.");
        if (content.length() > 500) throw new DomainException("내용은 500자 이하여야 합니다.");
    }
    public void update(String title, String content) {
        // PATCH에서 null은 기존 값 유지. 최종 값 모두를 먼저 검증한 후 변경한다.
        String nextTitle = title == null ? this.title : title;
        String nextContent = content == null ? this.content : content;
        validateTitle(nextTitle);
        validateContent(nextContent);
        this.title = nextTitle;
        this.content = nextContent;
    }
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public User getUser() { return user; }
}
