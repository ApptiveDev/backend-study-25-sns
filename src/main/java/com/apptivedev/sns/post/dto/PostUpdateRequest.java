package com.apptivedev.sns.post.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostUpdateRequest {

    @Size(max = 100, message = "제목은 100자 이하로 입력해 주세요.")
    private String title;

    @Size(max = 500, message = "내용은 500자 이하로 입력해 주세요.")
    private String content;

    private boolean titleProvided;
    private boolean contentProvided;

    public void setTitle(String title) {
        this.title = title;
        this.titleProvided = true;
    }

    public void setContent(String content) {
        this.content = content;
        this.contentProvided = true;
    }
}