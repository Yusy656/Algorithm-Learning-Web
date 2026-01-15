package com.smartlearning.service;

import com.smartlearning.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 题目数据访问接口
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * 根据知识点ID、题型和难度查找题目
     * @param knowledgePointId 知识点ID
     * @param type 题型
     * @param difficulty 难度
     * @return 题目列表
     */
    List<Question> findByKnowledgePointIdAndTypeAndDifficulty(Long knowledgePointId, String type, String difficulty);

    /**
     * 根据知识点ID和题型查找题目
     * @param knowledgePointId 知识点ID
     * @param type 题型
     * @return 题目列表
     */
    List<Question> findByKnowledgePointIdAndType(Long knowledgePointId, String type);
}