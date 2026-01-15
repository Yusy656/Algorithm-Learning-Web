package com.smartlearning.service;

import com.smartlearning.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 消息数据访问接口
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * 根据会话ID查找消息列表
     * @param conversationId 会话ID
     * @return 消息列表
     */
    List<Message> findByConversationIdOrderByTimestampAsc(Long conversationId);
}