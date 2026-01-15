package com.smartlearning.service;

import com.smartlearning.model.KnowledgePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 知识点数据访问接口
 */
@Repository
public interface KnowledgePointRepository extends JpaRepository<KnowledgePoint, Long> {
}