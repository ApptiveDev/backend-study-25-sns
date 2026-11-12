package com.apptivedev.sns.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.v3.oas.annotations.media.Schema;

public class PostUpdateRequest {
    private String title;
    private String content;
    private boolean titleProvided;
    private boolean contentProvided;

    // JSON에 키가 있으면 null이어도 호출된다. 생략과 명시적인 null을 구분한다.
    @JsonSetter("title")
    public void setTitle(String title) {
        this.title = title;
        titleProvided = true;
    }

    @JsonSetter("content")
    public void setContent(String content) {
        this.content = content;
        contentProvided = true;
    }

    @Schema(example = "수정한 제목", minLength = 1, maxLength = 100, description = "생략하면 유지, null 또는 공백만 있는 값은 거절")
    public String getTitle() {
        return title;
    }

    @Schema(example = "수정한 내용", minLength = 1, maxLength = 500, description = "생략하면 유지, null 또는 공백만 있는 값은 거절")
    public String getContent() {
        return content;
    }

    public boolean hasTitle() {
        return titleProvided;
    }

    public boolean hasContent() {
        return contentProvided;
    }
}
