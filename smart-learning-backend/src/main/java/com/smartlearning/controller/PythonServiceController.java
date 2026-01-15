package com.smartlearning.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Python服务模拟控制器
 * 用于在没有实际Python服务的情况下模拟Python服务的响应
 */
@RestController
@RequestMapping("/python-api")
public class PythonServiceController {

    /**
     * 模拟生成题目
     */
    @PostMapping("/question/generate")
    public Map<String, Object> generateQuestions(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "生成题目成功");
        
        List<Map<String, Object>> questions = new ArrayList<>();
        
        // 生成解答题
        if ("essay".equals(request.get("questionType"))) {
            int count = (int) request.get("count");
            String knowledgePointId = (String) request.get("knowledgePointId");
            String difficulty = (String) request.get("difficulty");
            
            for (int i = 0; i < count; i++) {
                Map<String, Object> question = new HashMap<>();
                question.put("id", "essay_" + System.currentTimeMillis() + "_" + i);
                question.put("type", "essay");
                question.put("content", "请详细解答以下问题：" + getKnowledgePointName(knowledgePointId) + "中关于" + difficulty + "难度的知识点。");
                question.put("score", difficulty.equals("simple") ? 10 : (difficulty.equals("medium") ? 15 : 20));
                questions.add(question);
            }
        }
        
        result.put("data", Map.of("questions", questions));
        return result;
    }
    
    /**
     * 模拟评估答案
     */
    @PostMapping("/question/evaluate")
    public Map<String, Object> evaluateAnswers(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "评分完成");
        
        List<Map<String, Object>> questions = (List<Map<String, Object>>) request.get("questions");
        
        // 简单评分逻辑
        int totalScore = 0;
        int correctCount = 0;
        
        for (Map<String, Object> question : questions) {
            if ("essay".equals(question.get("type"))) {
                String answer = (String) question.get("answer");
                // 简单判断答案长度
                if (answer != null && answer.length() > 50) {
                    correctCount++;
                    totalScore += 15; // 假设每题15分
                }
            }
        }
        
        result.put("data", Map.of(
                "score", totalScore,
                "correctCount", correctCount,
                "totalCount", questions.size()
        ));
        
        return result;
    }
    
    /**
     * 模拟知识解答
     */
    @PostMapping("/knowledge/ask")
    public Map<String, Object> askQuestion(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "获取答案成功");
        
        String content = (String) request.get("content");
        String sessionId = (String) request.get("sessionId");
        
        // 生成会话ID
        if (sessionId == null) {
            sessionId = "session_" + System.currentTimeMillis();
        }
        
        // 简单的答案生成逻辑
        String answer = generateAnswer(content);
        
        result.put("data", Map.of(
                "answer", answer,
                "sessionId", sessionId
        ));
        
        return result;
    }
    
    /**
     * 根据知识点ID获取知识点名称
     */
    private String getKnowledgePointName(String knowledgePointId) {
        Map<String, String> knowledgePoints = new HashMap<>();
        knowledgePoints.put("1", "数学");
        knowledgePoints.put("2", "代数");
        knowledgePoints.put("3", "几何");
        knowledgePoints.put("4", "物理");
        knowledgePoints.put("5", "力学");
        knowledgePoints.put("6", "电学");
        
        return knowledgePoints.getOrDefault(knowledgePointId, "未知知识点");
    }
    
    /**
     * 生成答案
     */
    private String generateAnswer(String question) {
        // 简单的关键词匹配
        if (question.contains("二次函数")) {
            return "二次函数是指最高次数为2的多项式函数，一般形式为：f(x) = ax² + bx + c，其中a、b、c为常数且a≠0。\n\n二次函数的图像是一条抛物线，当a>0时，抛物线开口向上；当a<0时，抛物线开口向下。\n\n二次函数的主要性质：\n1. 对称轴：x = -b/(2a)\n2. 顶点坐标：(-b/(2a), f(-b/(2a)))\n3. 判别式：Δ = b² - 4ac\n   - Δ>0时，函数有两个不同的实数根\n   - Δ=0时，函数有一个重根\n   - Δ<0时，函数没有实数根";
        } else if (question.contains("物理") || question.contains("力学")) {
            return "力学是物理学的一个分支，主要研究物体的运动和力的作用。\n\n力学的基本概念包括：\n1. 力：物体间的相互作用，可以改变物体的运动状态或使物体发生形变\n2. 质量：物体所含物质的多少，是物体惯性大小的量度\n3. 加速度：物体速度变化率的物理量\n\n牛顿三大定律是力学的基础：\n1. 牛顿第一定律（惯性定律）：一个物体如果不受外力作用，将保持静止状态或匀速直线运动状态\n2. 牛顿第二定律：物体的加速度与所受的合外力成正比，与物体的质量成反比\n3. 牛顿第三定律：作用力与反作用力大小相等，方向相反，作用在不同物体上";
        } else if (question.contains("化学") || question.contains("方程式")) {
            return "化学方程式是用化学式表示化学反应的式子，它反映了化学反应中反应物和生成物之间的质量关系和物质的量关系。\n\n化学方程式的配平原则：\n1. 质量守恒定律：反应前后各元素的原子种类和数目不变\n2. 电荷守恒：对于离子反应，反应前后电荷总数相等\n\n配平化学方程式的常用方法：\n1. 观察法：通过观察直接配平简单的化学方程式\n2. 最小公倍数法：找出反应前后各元素原子数的最小公倍数进行配平\n3. 奇数配偶法：将奇数原子变为偶数，再进行配平\n4. 氧化还原法：根据氧化还原反应中电子转移的数目进行配平";
        } else {
            return "您好！我是智能学习助手，可以帮您解答各种学习问题。您的问题是：\"" + question + "\"\n\n这是一个模拟的回答。在实际应用中，这里会调用大语言模型来生成更准确、更详细的答案。\n\n如果您有更具体的问题，请提供更多细节，我会尽力为您提供更好的解答。";
        }
    }
}