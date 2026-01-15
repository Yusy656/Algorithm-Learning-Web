package com.smartlearning.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Python服务调用客户端
 */
@Component
public class PythonServiceClient {

    @Value("${python.service.url}")
    private String pythonServiceUrl;

    @Value("${python.service.timeout}")
    private int timeout;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public PythonServiceClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 调用Python服务生成题目
     * @param knowledgePointId 知识点ID
     * @param difficulty 难度
     * @param questionType 题型
     * @param count 题目数量
     * @param expansionLevel 知识拓展度
     * @return 响应结果
     */
    public Map<String, Object> generateQuestions(String knowledgePointId, String difficulty, 
                                               String questionType, Integer count, String expansionLevel) {
        try {
            String url = pythonServiceUrl + "/python-api/question/generate";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("knowledgePointId", knowledgePointId);
            requestBody.put("difficulty", difficulty);
            requestBody.put("questionType", questionType);
            requestBody.put("count", count);
            requestBody.put("expansionLevel", expansionLevel);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Python服务调用失败，状态码：" + response.getStatusCodeValue());
                return errorResponse;
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Python服务调用异常：" + e.getMessage());
            return errorResponse;
        }
    }

    /**
     * 调用Python服务评估答案
     * @param questions 题目及答案列表
     * @return 响应结果
     */
    public Map<String, Object> evaluateAnswers(Map<String, Object> questions) {
        try {
            String url = pythonServiceUrl + "/python-api/question/evaluate";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(questions, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Python服务调用失败，状态码：" + response.getStatusCodeValue());
                return errorResponse;
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Python服务调用异常：" + e.getMessage());
            return errorResponse;
        }
    }

    /**
     * 调用Python服务进行知识解答
     * @param content 问题内容
     * @param image 图片数据（base64编码）
     * @param sessionId 会话ID
     * @return 响应结果
     */
    public Map<String, Object> askQuestion(String content, String image, String sessionId) {
        try {
            String url = pythonServiceUrl + "/python-api/knowledge/ask";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", content);
            if (image != null) {
                requestBody.put("image", image);
            }
            if (sessionId != null) {
                requestBody.put("sessionId", sessionId);
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Python服务调用失败，状态码：" + response.getStatusCodeValue());
                return errorResponse;
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Python服务调用异常：" + e.getMessage());
            return errorResponse;
        }
    }
}