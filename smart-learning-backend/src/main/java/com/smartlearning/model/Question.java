package com.smartlearning.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * 题目实体类
 */
@Entity
@Table(name = "question")
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "type", nullable = false, length = 20)
    private String type; // single_choice, multiple_choice, fill_blank, essay

    @Column(name = "difficulty", nullable = false, length = 20)
    private String difficulty; // simple, medium, difficult

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "answer")
    private String answer; // 存储答案，对于选择题存储选项ID，对于填空题和解答题存储文本答案

    @Column(name = "analysis")
    private String analysis; // 题目解析

    @ManyToOne
    @JoinColumn(name = "knowledge_point_id")
    private KnowledgePoint knowledgePoint;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuestionOption> options;

    // getter and setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public KnowledgePoint getKnowledgePoint() {
        return knowledgePoint;
    }

    public void setKnowledgePoint(KnowledgePoint knowledgePoint) {
        this.knowledgePoint = knowledgePoint;
    }

    public List<QuestionOption> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionOption> options) {
        this.options = options;
    }
}