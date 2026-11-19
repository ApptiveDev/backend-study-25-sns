package com.apptivedev.sns.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

public class UpdatePostRequest {

    @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
    private String title;

    @Size(max = 500, message = "내용은 500자 이하여야 합니다.")
    private String content;

    private boolean titleProvided;
    private boolean contentProvided;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public boolean hasTitle() {
        return titleProvided;
    }

    public boolean hasContent() {
        return contentProvided;
    }

    @JsonIgnore
    @AssertTrue(message = "수정할 항목을 보내주세요.")
    public boolean isAnyFieldProvided() {
        return titleProvided || contentProvided;
    }

    // PATCH는 생략한 필드를 허용하고, 명시적으로 보낸 null·공백은 거절한다.
    @JsonIgnore
    @AssertTrue(message = "제목은 비어 있을 수 없습니다.")
    public boolean isTitleValid() {
        return !titleProvided || (title != null && !title.isBlank());
    }

    @JsonIgnore
    @AssertTrue(message = "내용은 비어 있을 수 없습니다.")
    public boolean isContentValid() {
        return !contentProvided || (content != null && !content.isBlank());
    }

    public void setTitle(String title) {
        this.title = title;
        this.titleProvided = true;
    }

    public void setContent(String content) {
        this.content = content;
        this.contentProvided = true;
    }
}
