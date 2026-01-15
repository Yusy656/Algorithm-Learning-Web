package com.smartlearning.dto;

/**
 * 知识解答请求DTO
 */
public class KnowledgeAskRequest {

    private String content;
    private String image;
    private String sessionId;

    // getter and setter
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}