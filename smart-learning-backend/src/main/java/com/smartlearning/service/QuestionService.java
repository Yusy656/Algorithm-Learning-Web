package com.smartlearning.service;

import com.smartlearning.model.Question;
import com.smartlearning.model.KnowledgePoint;
import com.smartlearning.dto.QuestionGenerateRequest;
import com.smartlearning.dto.QuestionSubmitRequest;
import com.smartlearning.util.PythonServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 题目服务类
 */
@Service
@Transactional
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private KnowledgePointRepository knowledgePointRepository;

    @Autowired
    private PythonServiceClient pythonServiceClient;

    /**
     * 获取知识点列表
     * @return 知识点列表
     */
    public Map<String, Object> getKnowledgePoints() {
        Map<String, Object> result = new HashMap<>();

        try {
            List<KnowledgePoint> knowledgePoints = knowledgePointRepository.findAll();
            List<Map<String, Object>> points = new ArrayList<>();

            for (KnowledgePoint point : knowledgePoints) {
                Map<String, Object> pointMap = new HashMap<>();
                pointMap.put("id", point.getId());
                pointMap.put("name", point.getName());
                points.add(pointMap);
            }

            result.put("success", true);
            result.put("message", "获取知识点列表成功");
            result.put("data", Map.of("knowledgePoints", points));
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取知识点列表失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 生成题目
     * @param request 生成题目请求
     * @return 生成结果
     */
    public Map<String, Object> generateQuestions(QuestionGenerateRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查是否为解答题，如果是则调用Python服务
            if ("essay".equals(request.getQuestionType())) {
                // 调用Python服务生成题目
                return pythonServiceClient.generateQuestions(
                        request.getKnowledgePointId(),
                        request.getDifficulty(),
                        request.getQuestionType(),
                        request.getCount(),
                        request.getExpansionLevel()
                );
            }

            // 其他题型从数据库中随机选择
            Long knowledgePointId = Long.parseLong(request.getKnowledgePointId());
            List<Question> questions = questionRepository.findByKnowledgePointIdAndTypeAndDifficulty(
                    knowledgePointId,
                    request.getQuestionType(),
                    request.getDifficulty()
            );

            // 如果题目数量不足，从其他难度补充
            if (questions.size() < request.getCount()) {
                List<Question> additionalQuestions = questionRepository.findByKnowledgePointIdAndType(
                        knowledgePointId,
                        request.getQuestionType()
                );

                // 去重
                Set<Long> existingIds = new HashSet<>();
                for (Question q : questions) {
                    existingIds.add(q.getId());
                }

                for (Question q : additionalQuestions) {
                    if (!existingIds.contains(q.getId()) && questions.size() < request.getCount()) {
                        questions.add(q);
                        existingIds.add(q.getId());
                    }
                }
            }

            // 随机打乱题目顺序
            Collections.shuffle(questions);

            // 截取指定数量的题目
            int actualCount = Math.min(questions.size(), request.getCount());
            List<Question> selectedQuestions = questions.subList(0, actualCount);

            // 构建返回数据
            List<Map<String, Object>> questionList = new ArrayList<>();
            for (Question q : selectedQuestions) {
                Map<String, Object> questionMap = new HashMap<>();
                questionMap.put("id", q.getId());
                questionMap.put("type", q.getType());
                questionMap.put("content", q.getContent());

                // 添加选项（如果有）
                if ("single_choice".equals(q.getType()) || "multiple_choice".equals(q.getType())) {
                    List<Map<String, Object>> options = new ArrayList<>();
                    q.getOptions().forEach(option -> {
                        Map<String, Object> optionMap = new HashMap<>();
                        optionMap.put("id", option.getOptionId());
                        optionMap.put("content", option.getContent());
                        options.add(optionMap);
                    });
                    questionMap.put("options", options);
                }

                questionMap.put("score", q.getScore());
                questionList.add(questionMap);
            }

            result.put("success", true);
            result.put("message", "生成题目成功");
            result.put("data", Map.of("questions", questionList));
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成题目失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 提交答案
     * @param request 提交答案请求
     * @return 评分结果
     */
    public Map<String, Object> submitAnswers(QuestionSubmitRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查是否包含解答题
            boolean hasEssayQuestions = request.getQuestions().stream()
                    .anyMatch(q -> "essay".equals(q.getType()));

            // 如果包含解答题，调用Python服务进行评分
            if (hasEssayQuestions) {
                // 构建请求参数
                Map<String, Object> params = new HashMap<>();
                params.put("questions", request.getQuestions());

                // 调用Python服务进行评分
                return pythonServiceClient.evaluateAnswers(params);
            }

            // 其他题型在后端进行评分
            int totalScore = 0;
            int correctCount = 0;
            int totalCount = request.getQuestions().size();

            for (QuestionSubmitRequest.QuestionAnswer answer : request.getQuestions()) {
                Question question = questionRepository.findById(Long.parseLong(answer.getId())).orElse(null);
                if (question != null) {
                    boolean isCorrect = false;

                    // 根据题型进行不同的评分逻辑
                    switch (question.getType()) {
                        case "single_choice":
                            isCorrect = question.getAnswer().equals(answer.getAnswer());
                            break;
                        case "multiple_choice":
                            // 多选题需要完全匹配
                            Set<String> correctAnswers = new HashSet<>(
                                    Arrays.asList(question.getAnswer().split(","))
                            );
                            Set<String> userAnswers = new HashSet<>();
                            if (answer.getAnswer() instanceof List) {
                                userAnswers.addAll((List<String>) answer.getAnswer());
                            } else if (answer.getAnswer() instanceof String) {
                                userAnswers.addAll(Arrays.asList(((String) answer.getAnswer()).split(",")));
                            }
                            isCorrect = correctAnswers.equals(userAnswers);
                            break;
                        case "fill_blank":
                            // 填空题可以有一定的容错率，这里简单处理为完全匹配
                            isCorrect = question.getAnswer().equalsIgnoreCase((String) answer.getAnswer());
                            break;
                    }

                    if (isCorrect) {
                        correctCount++;
                        totalScore += question.getScore();
                    }
                }
            }

            result.put("success", true);
            result.put("message", "评分完成");
            result.put("data", Map.of(
                    "score", totalScore,
                    "correctCount", correctCount,
                    "totalCount", totalCount
            ));
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "评分失败：" + e.getMessage());
        }

        return result;
    }
}