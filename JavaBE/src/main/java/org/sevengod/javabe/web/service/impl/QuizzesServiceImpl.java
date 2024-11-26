package org.sevengod.javabe.web.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.sevengod.javabe.entity.QuizSubmission;
import org.sevengod.javabe.entity.Quizzes;
import org.sevengod.javabe.mapper.QuizSubmissionMapper;
import org.sevengod.javabe.web.mapper.QuizzesMapper;
import org.sevengod.javabe.web.service.DifyService;
import org.sevengod.javabe.web.service.QuizzesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class QuizzesServiceImpl extends ServiceImpl<QuizzesMapper, Quizzes> implements QuizzesService {
    
    @Autowired
    private DifyService difyService;

    @Autowired
    private QuizSubmissionMapper quizSubmissionMapper;

    private final String APIKey = "app-RjU3aR6XQ5Dd91QmeGF8UMoK";

    @Override
    public Map<String, Object> getQuizWithDifyResponse(Long chapterId, Long userId) {
        // 使用chapterId查询Quiz
        LambdaQueryWrapper<Quizzes> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Quizzes::getChapterId, chapterId);
        Quizzes quiz = this.getOne(queryWrapper);
        
        Map<String, Object> result = new HashMap<>();
        
        if (quiz != null) {
            result.put("quiz_id", quiz.getQuizId());
            result.put("chapter_id", quiz.getChapterId());
            result.put("title", quiz.getTitle());
            result.put("quiz_prompt", quiz.getQuizPrompt());
            
            try {
                String aiResponse = difyService.streamWorkflow(
                        Map.of("Content", quiz.getQuizPrompt(),
                                "Type", "小测"
                        ),
                        userId.toString(),
                        APIKey
                )
                        .collectList()
                        .block()  // 等待所有响应
                        .stream()
                        .filter(response -> "workflow_finished".equals(response.getEvent()))
                        .findFirst()
                        .<String>map(response -> {
                            return (String) response.getData().getOutputs().get("answer");
                        })
                        .orElseThrow(() -> new RuntimeException("未能获取到有效的生成内容"));

                // 清理JSON字符串，去除markdown标记
                aiResponse = aiResponse.replaceAll("```json\\n", "")
                                    .replaceAll("```", "")
                                    .trim();

                // 使用Fastjson2解析JSON数据并添加到结果中
                Map<String, Object> quizData = JSON.parseObject(aiResponse);
                result.putAll(quizData);
                
            } catch (Exception e) {
                result.put("error", "Error processing quiz data: " + e.getMessage());
            }
        }
        
        return result;
    }

    @Override
    public Map<String, Object> submitAnswerAndScore(Long quizId, Long userId, String questions, String answers, BigDecimal score) {
        // 1. 创建提交记录
        QuizSubmission submission = new QuizSubmission();
        submission.setQuizId(quizId);
        submission.setUserId(userId);
        submission.setGeneratedQuestions(questions);
        submission.setAnswers(answers);
        submission.setScore(score);
        submission.setSubmittedAt(LocalDateTime.now());

        // 2. 保存到数据库
        quizSubmissionMapper.insert(submission);

        // 3. 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("submissionId", submission.getSubmissionId());
        result.put("score", submission.getScore());
        result.put("submittedAt", submission.getSubmittedAt());
        
        return result;
    }
}
