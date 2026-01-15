package com.smartlearning.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 题目选项实体类
 */
@Entity
@Table(name = "question_option")
public class QuestionOption implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_id", nullable = false, length = 10)
    private String optionId; // 选项标识，如A、B、C、D

    @Column(name = "content", nullable = false)
    private String content; // 选项内容

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    // getter and setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}