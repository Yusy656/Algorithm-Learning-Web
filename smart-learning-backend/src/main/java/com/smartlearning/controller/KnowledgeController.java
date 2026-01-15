package com.smartlearning.controller;

import com.smartlearning.dto.ApiResponse;
import com.smartlearning.dto.KnowledgeAskRequest;
import com.smartlearning.service.KnowledgeService;
import com.smartlearning.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 知识解答控制器
 */
@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    @Autowired
    private KnowledgeService knowledgeService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 发送问题
     * @param request 问题请求
     * @param token JWT token
     * @return 解答结果
     */
    @PostMapping("/ask")
    public ApiResponse<Map<String, Object>> askQuestion(@RequestBody KnowledgeAskRequest request, @RequestHeader("Authorization") String token) {
        try {
            // 从token中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token.substring(7)); // 去掉"Bearer "前缀
            
            Map<String, Object> result = knowledgeService.askQuestion(request, userId);
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ApiResponse.success(result.get("message").toString(), (Map<String, Object>) result.get("data"));
            } else {
                return ApiResponse.fail(result.get("message").toString());
            }
        } catch (Exception e) {
            return ApiResponse.fail("获取答案失败：" + e.getMessage());
        }
    }

    /**
     * 获取历史对话列表
     * @param token JWT token
     * @return 历史对话列表
     */
    @GetMapping("/history")
    public ApiResponse<Map<String, Object>> getHistoryConversations(@RequestHeader("Authorization") String token) {
        try {
            // 从token中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token.substring(7)); // 去掉"Bearer "前缀
            
            Map<String, Object> result = knowledgeService.getHistoryConversations(userId);
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ApiResponse.success(result.get("message").toString(), (Map<String, Object>) result.get("data"));
            } else {
                return ApiResponse.fail(result.get("message").toString());
            }
        } catch (Exception e) {
            return ApiResponse.fail("获取历史对话列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取对话详情
     * @param conversationId 对话ID
     * @param token JWT token
     * @return 对话详情
     */
    @GetMapping("/conversation/{id}")
    public ApiResponse<Map<String, Object>> getConversationDetail(@PathVariable("id") Long conversationId, @RequestHeader("Authorization") String token) {
        try {
            // 从token中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token.substring(7)); // 去掉"Bearer "前缀
            
            Map<String, Object> result = knowledgeService.getConversationDetail(conversationId, userId);
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ApiResponse.success(result.get("message").toString(), (Map<String, Object>) result.get("data"));
            } else {
                return ApiResponse.fail(result.get("message").toString());
            }
        } catch (Exception e) {
            return ApiResponse.fail("获取对话详情失败：" + e.getMessage());
        }
    }
}