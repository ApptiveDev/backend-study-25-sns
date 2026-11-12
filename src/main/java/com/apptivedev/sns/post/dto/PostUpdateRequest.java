package com.apptivedev.sns.post.dto;

import com.fasterxml.jackson.annotation.JsonSetter;

public class PostUpdateRequest {

    private String title;
    private String content;

    private boolean titlePresent;
    private boolean contentPresent;

    public PostUpdateRequest() {
    }

    @JsonSetter("title")
    public void setTitle(String title) {
        this.title = title;
        this.titlePresent = true;
    }

    @JsonSetter("content")
    public void setContent(String content) {
        this.content = content;
        this.contentPresent = true;
    }

    public String title() {
        return title;
    }

    public String content() {
        return content;
    }

    public boolean hasTitle() {
        return titlePresent;
    }

    public boolean hasContent() {
        return contentPresent;
    }
}
