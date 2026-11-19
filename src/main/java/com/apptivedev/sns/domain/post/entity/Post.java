package com.apptivedev.sns.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    // ---------- 테이블 컬럼 설계 ----------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)    // DB가 1, 2, 3... 자동 부여
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    // ---------- 새 게시글 생성 (id는 DB가 정하므로 받지 않음->중요!) ----------
    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // ---------- 수정 (setter 대신 의미 있는 이름의 메서드) ----------
    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }
}