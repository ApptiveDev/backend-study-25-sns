package com.apptivedev.sns.post.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.Size;

public class PostUpdateRequest {

    @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
    private String title;

    @Size(max = 500, message = "본문은 500자 이하여야 합니다.")
    private String content;

    public String getTitle() {
        return title;
    }

    @JsonSetter(nulls = Nulls.FAIL)
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    @JsonSetter(nulls = Nulls.FAIL)
    public void setContent(String content) {
        this.content = content;
    }
}