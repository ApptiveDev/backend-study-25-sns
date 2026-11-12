package com.apptivedev.sns.post.entity;
// 같은 이름을 가진 클래스들을 구분함.
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String title;

    @Column(length = 500)
    private String content;

    // JPA가 엔티티를 생성할 때 사용하는 기본 생성자
    // 흠...
    protected Post() {
    }

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void update(String title, String content) {
        if (title != null) {
            this.title = title;
        }
        // 아무것도 받지 않으면 그걸로 덮어씌우는 게 아니라
        // 기존 내용을 변경하지 않고 유지함.

        if (content != null) {
            this.content = content;
        }
    }
}