package com.smartlearning.dto;

import java.util.List;

/**
 * 提交答案请求DTO
 */
public class QuestionSubmitRequest {

    private List<QuestionAnswer> questions;

    // getter and setter
    public List<QuestionAnswer> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionAnswer> questions) {
        this.questions = questions;
    }

    /**
     * 题目答案DTO
     */
    public static class QuestionAnswer {
        private String id;
        private String type;
        private Object answer;

        // getter and setter
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Object getAnswer() {
            return answer;
        }

        public void setAnswer(Object answer) {
            this.answer = answer;
        }
    }
}