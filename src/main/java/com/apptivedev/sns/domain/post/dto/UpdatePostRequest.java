package com.apptivedev.sns.domain.post.dto;

public class UpdatePostRequest {

    private String title;
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

    public void setTitle(String title) {
        this.title = title;
        this.titleProvided = true;
    }

    public void setContent(String content) {
        this.content = content;
        this.contentProvided = true;
    }
}
