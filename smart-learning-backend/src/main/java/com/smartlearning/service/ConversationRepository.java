package com.smartlearning.service;

import com.smartlearning.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 会话数据访问接口
 */
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    /**
     * 根据会话ID查找会话
     * @param sessionId 会话ID
     * @return 会话
     */
    Conversation findBySessionId(String sessionId);

    /**
     * 根据用户ID查找会话列表
     * @param userId 用户ID
     * @return 会话列表
     */
    List<Conversation> findByUserIdOrderByUpdateTimeDesc(Long userId);
}