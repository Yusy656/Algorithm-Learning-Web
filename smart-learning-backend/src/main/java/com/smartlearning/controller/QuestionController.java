package com.smartlearning.controller;

import com.smartlearning.dto.ApiResponse;
import com.smartlearning.dto.QuestionGenerateRequest;
import com.smartlearning.dto.QuestionSubmitRequest;
import com.smartlearning.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 题目控制器
 */
@RestController
@RequestMapping("/api/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * 获取知识点列表
     * @return 知识点列表
     */
    @GetMapping("/knowledge-points")
    public ApiResponse<Map<String, Object>> getKnowledgePoints() {
        try {
            Map<String, Object> result = questionService.getKnowledgePoints();
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ApiResponse.success(result.get("message").toString(), (Map<String, Object>) result.get("data"));
            } else {
                return ApiResponse.fail(result.get("message").toString());
            }
        } catch (Exception e) {
            return ApiResponse.fail("获取知识点列表失败：" + e.getMessage());
        }
    }

    /**
     * 生成题目
     * @param request 生成题目请求
     * @return 生成结果
     */
    @PostMapping("/generate")
    public ApiResponse<Map<String, Object>> generateQuestions(@RequestBody QuestionGenerateRequest request) {
        try {
            Map<String, Object> result = questionService.generateQuestions(request);
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ApiResponse.success(result.get("message").toString(), (Map<String, Object>) result.get("data"));
            } else {
                return ApiResponse.fail(result.get("message").toString());
            }
        } catch (Exception e) {
            return ApiResponse.fail("生成题目失败：" + e.getMessage());
        }
    }

    /**
     * 提交答案
     * @param request 提交答案请求
     * @return 评分结果
     */
    @PostMapping("/submit")
    public ApiResponse<Map<String, Object>> submitAnswers(@RequestBody QuestionSubmitRequest request) {
        try {
            Map<String, Object> result = questionService.submitAnswers(request);
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ApiResponse.success(result.get("message").toString(), (Map<String, Object>) result.get("data"));
            } else {
                return ApiResponse.fail(result.get("message").toString());
            }
        } catch (Exception e) {
            return ApiResponse.fail("评分失败：" + e.getMessage());
        }
    }
}