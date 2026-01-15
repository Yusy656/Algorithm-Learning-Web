package com.smartlearning.service;

import com.smartlearning.model.Conversation;
import com.smartlearning.model.Message;
import com.smartlearning.model.User;
import com.smartlearning.dto.KnowledgeAskRequest;
import com.smartlearning.util.PythonServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 知识解答服务类
 */
@Service
@Transactional
public class KnowledgeService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PythonServiceClient pythonServiceClient;

    /**
     * 发送问题
     * @param request 问题请求
     * @param userId 用户ID
     * @return 解答结果
     */
    public Map<String, Object> askQuestion(KnowledgeAskRequest request, Long userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 调用Python服务获取答案
            Map<String, Object> pythonResult = pythonServiceClient.askQuestion(
                    request.getContent(),
                    request.getImage(),
                    request.getSessionId()
            );

            if (!Boolean.TRUE.equals(pythonResult.get("success"))) {
                return pythonResult;
            }

            // 获取Python服务返回的数据
            String answer = (String) ((Map<String, Object>) pythonResult.get("data")).get("answer");
            String sessionId = (String) ((Map<String, Object>) pythonResult.get("data")).get("sessionId");

            // 查找或创建会话
            Conversation conversation = null;
            if (request.getSessionId() != null) {
                conversation = conversationRepository.findBySessionId(request.getSessionId());
            }

            if (conversation == null) {
                conversation = new Conversation();
                conversation.setSessionId(sessionId);
                conversation.setTitle(request.getContent().length() > 50 ? 
                        request.getContent().substring(0, 50) + "..." : request.getContent());
                
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    conversation.setUser(user);
                }

                conversation = conversationRepository.save(conversation);
            } else {
                // 更新会话标题（如果是新会话的第一个问题）
                if (conversation.getTitle() == null || conversation.getTitle().isEmpty()) {
                    conversation.setTitle(request.getContent().length() > 50 ? 
                            request.getContent().substring(0, 50) + "..." : request.getContent());
                    conversationRepository.save(conversation);
                }
            }

            // 保存用户问题
            Message userMessage = new Message();
            userMessage.setRole("user");
            userMessage.setContent(request.getContent());
            userMessage.setImage(request.getImage());
            userMessage.setTimestamp(new Date());
            userMessage.setConversation(conversation);
            messageRepository.save(userMessage);

            // 保存AI回答
            Message aiMessage = new Message();
            aiMessage.setRole("assistant");
            aiMessage.setContent(answer);
            aiMessage.setTimestamp(new Date());
            aiMessage.setConversation(conversation);
            messageRepository.save(aiMessage);

            result.put("success", true);
            result.put("message", "获取答案成功");
            result.put("data", Map.of(
                    "answer", answer,
                    "sessionId", sessionId
            ));
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取答案失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 获取历史对话列表
     * @param userId 用户ID
     * @return 历史对话列表
     */
    public Map<String, Object> getHistoryConversations(Long userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Conversation> conversations = conversationRepository.findByUserIdOrderByUpdateTimeDesc(userId);
            List<Map<String, Object>> conversationList = new ArrayList<>();

            for (Conversation conversation : conversations) {
                Map<String, Object> conversationMap = new HashMap<>();
                conversationMap.put("id", conversation.getId());
                conversationMap.put("sessionId", conversation.getSessionId());
                conversationMap.put("title", conversation.getTitle());
                conversationMap.put("timestamp", conversation.getUpdateTime().getTime());
                conversationList.add(conversationMap);
            }

            result.put("success", true);
            result.put("message", "获取历史对话列表成功");
            result.put("data", Map.of("conversations", conversationList));
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取历史对话列表失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 获取对话详情
     * @param conversationId 对话ID
     * @param userId 用户ID
     * @return 对话详情
     */
    public Map<String, Object> getConversationDetail(Long conversationId, Long userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 查找对话
            Conversation conversation = conversationRepository.findById(conversationId).orElse(null);
            if (conversation == null) {
                result.put("success", false);
                result.put("message", "对话不存在");
                return result;
            }

            // 验证对话所属用户
            if (!conversation.getUser().getId().equals(userId)) {
                result.put("success", false);
                result.put("message", "无权访问该对话");
                return result;
            }

            // 获取对话消息
            List<Message> messages = messageRepository.findByConversationIdOrderByTimestampAsc(conversationId);
            List<Map<String, Object>> messageList = new ArrayList<>();

            for (Message message : messages) {
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("role", message.getRole());
                messageMap.put("content", message.getContent());
                if (message.getImage() != null) {
                    messageMap.put("image", message.getImage());
                }
                messageMap.put("timestamp", message.getTimestamp().getTime());
                messageList.add(messageMap);
            }

            result.put("success", true);
            result.put("message", "获取对话详情成功");
            result.put("data", Map.of("messages", messageList));
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取对话详情失败：" + e.getMessage());
        }

        return result;
    }
}