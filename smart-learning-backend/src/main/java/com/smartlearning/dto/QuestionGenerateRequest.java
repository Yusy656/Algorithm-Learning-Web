package com.smartlearning.dto;

/**
 * 生成题目请求DTO
 */
public class QuestionGenerateRequest {

    private String knowledgePointId;
    private String difficulty;
    private String questionType;
    private Integer count;
    private String expansionLevel;

    // getter and setter
    public String getKnowledgePointId() {
        return knowledgePointId;
    }

    public void setKnowledgePointId(String knowledgePointId) {
        this.knowledgePointId = knowledgePointId;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getExpansionLevel() {
        return expansionLevel;
    }

    public void setExpansionLevel(String expansionLevel) {
        this.expansionLevel = expansionLevel;
    }
}