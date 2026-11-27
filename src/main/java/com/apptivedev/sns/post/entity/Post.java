package com.apptivedev.sns.post.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(length = 500)
    private String content;

    // JPA가 사용하는 기본 생성자
    protected Post() {
    }

    // 제목과 내용을 전달받아 객체를 만드는 생성자
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

        if (content != null) {
            this.content = content;
        }
    }
}