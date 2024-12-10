package org.sevengod.javabe.web.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.sevengod.javabe.entity.Chapter;
import org.sevengod.javabe.entity.QuizSubmission;
import org.sevengod.javabe.entity.Quizzes;
import org.sevengod.javabe.web.exception.DifyException;
import org.sevengod.javabe.web.mapper.ChapterMapper;
import org.sevengod.javabe.web.mapper.QuizSubmissionMapper;
import org.sevengod.javabe.web.mapper.QuizzesMapper;
import org.sevengod.javabe.web.service.DifyService;
import org.sevengod.javabe.web.service.QuizzesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@Service
public class QuizzesServiceImpl extends ServiceImpl<QuizzesMapper, Quizzes> implements QuizzesService {
    
    private static final Logger log = LoggerFactory.getLogger(QuizzesServiceImpl.class);

    @Autowired
    private DifyService difyService;

    @Autowired
    private QuizSubmissionMapper quizSubmissionMapper;

    @Autowired
    private ChapterMapper chapterMapper;

    private final String APIKey = "app-RjU3aR6XQ5Dd91QmeGF8UMoK";

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Map<String, Object>> getQuizWithDifyResponse(Long chapterId, Long userId) {
        log.info("开始异步生成测验 - 章节ID: {}, 线程: {}", chapterId, Thread.currentThread().getName());
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 使用chapterId查询Quiz
            LambdaQueryWrapper<Quizzes> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Quizzes::getChapterId, chapterId);
            Quizzes quiz = this.getOne(queryWrapper);
            
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
                            .block(Duration.ofSeconds(30))  // 设置30秒超时
                            .stream()
                            .filter(response -> "workflow_finished".equals(response.getEvent()))
                            .findFirst()
                            .<String>map(response -> {
                                return (String) response.getData().getOutputs().get("answer");
                            })
                            .orElseThrow(() -> new RuntimeException("未能获取到有效的生成内容"));

                    // 清理JSON字符串
                    aiResponse = aiResponse.replaceAll("```json\\n", "")
                                      .replaceAll("```", "")
                                      .trim();

                    // 解析JSON数据并添加到结果中
                    Map<String, Object> quizData = JSON.parseObject(aiResponse);
                    result.putAll(quizData);
                    
                } catch (Exception e) {
                    String errorDetails = "生成测验失败: " + e.getMessage();
                    result.put("error", errorDetails);
                    if (e instanceof TimeoutException) {
                        throw DifyException.timeout();
                    }
                    throw new DifyException(errorDetails, e);
                }
            }
            
            log.info("完成测验生成 - 章节ID: {}, 线程: {}", chapterId, Thread.currentThread().getName());
            return CompletableFuture.completedFuture(result);
            
        } catch (Exception e) {
            log.error("测验生成失败 - 章节ID: {}, 线程: {}, 错误: {}", 
                     chapterId, Thread.currentThread().getName(), e.getMessage());
            result.put("error", "处理测验数据时出错: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }



    @Override
    public Map<String, Object> submitAnswerAndScore(Long quizId, Long userId, String questions, BigDecimal score,Integer timeLeft) {
        // 检查是否已经提交过
        LambdaQueryWrapper<QuizSubmission> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(QuizSubmission::getQuizId, quizId)
                   .eq(QuizSubmission::getUserId, userId);
        
        if (quizSubmissionMapper.exists(checkWrapper)) {
            throw new IllegalStateException("您已经提交过这个测验，不能重复提交");
        }

        // 1. 创建提交记录
        QuizSubmission submission = new QuizSubmission();
        submission.setQuizId(quizId);
        submission.setUserId(userId);
        submission.setGeneratedQuestions(questions);
        submission.setScore(score);
        submission.setTimeLeft(timeLeft);
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

    @Override
    public Map<String, Object> getUnitQuizScores(Long unitId, Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取该单元的所有章节
        LambdaQueryWrapper<Chapter> chapterWrapper = new LambdaQueryWrapper<>();
        chapterWrapper.eq(Chapter::getUnitId, unitId);
        List<Chapter> chapters = chapterMapper.selectList(chapterWrapper);
        
        // 存储每个章节的测验分数
        List<Map<String, Object>> chapterScores = new ArrayList<>();
        
        for (Chapter chapter : chapters) {
            Map<String, Object> chapterScore = new HashMap<>();
            chapterScore.put("chapter_id", chapter.getChapterId());
            chapterScore.put("chapter_title", chapter.getTitle());
            
            // 获取章节对应的测验
            LambdaQueryWrapper<Quizzes> quizWrapper = new LambdaQueryWrapper<>();
            quizWrapper.eq(Quizzes::getChapterId, chapter.getChapterId());
            Quizzes quiz = this.getOne(quizWrapper);
            
            if (quiz != null) {
                // 获取用户的测验提交记录
                LambdaQueryWrapper<QuizSubmission> submissionWrapper = new LambdaQueryWrapper<>();
                submissionWrapper.eq(QuizSubmission::getQuizId, quiz.getQuizId())
                        .eq(QuizSubmission::getUserId, userId)
                        .orderByDesc(QuizSubmission::getSubmittedAt)
                        .last("LIMIT 1");
                QuizSubmission submission = quizSubmissionMapper.selectOne(submissionWrapper);
                
                if (submission != null) {
                    chapterScore.put("quiz_id", quiz.getQuizId());
                    chapterScore.put("score", submission.getScore());
                    chapterScore.put("submission_time", submission.getSubmittedAt());
                } else {
                    chapterScore.put("quiz_id", quiz.getQuizId());
                    chapterScore.put("score", null);
                    chapterScore.put("submission_time", null);
                }
            } else {
                chapterScore.put("quiz_id", null);
                chapterScore.put("score", null);
                chapterScore.put("submission_time", null);
            }
            
            chapterScores.add(chapterScore);
        }
        
        result.put("unit_id", unitId);
        result.put("chapter_scores", chapterScores);
        
        return result;
    }

    @Override
    public Map<String, Object> getChapterQuizScores(Long chapterId, Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 获取章节信息
            Chapter chapter = chapterMapper.selectById(chapterId);
            if (chapter == null) {
                result.put("success", false);
                result.put("message", "章节不存在");
                return result;
            }

            // 2. 获取章节对应的测验
            LambdaQueryWrapper<Quizzes> quizWrapper = new LambdaQueryWrapper<>();
            quizWrapper.eq(Quizzes::getChapterId, chapterId);
            Quizzes quiz = this.getOne(quizWrapper);

            if (quiz == null) {
                result.put("success", true);
                result.put("chapterId", chapterId);
                result.put("chapterTitle", chapter.getTitle());
                result.put("hasQuiz", false);
                return result;
            }

            // 3. 获取用户的最新提交记录
            LambdaQueryWrapper<QuizSubmission> submissionWrapper = new LambdaQueryWrapper<>();
            submissionWrapper.eq(QuizSubmission::getQuizId, quiz.getQuizId())
                           .eq(QuizSubmission::getUserId, userId)
                           .orderByDesc(QuizSubmission::getSubmittedAt);
            
            QuizSubmission latestSubmission = quizSubmissionMapper.selectOne(submissionWrapper);

            // 4. 构建返回结果
            result.put("success", true);
            result.put("chapterId", chapterId);
            result.put("chapterTitle", chapter.getTitle());
            result.put("hasQuiz", true);
            result.put("quizId", quiz.getQuizId());
            result.put("quizTitle", quiz.getTitle());
            
            if (latestSubmission != null) {
                result.put("score", latestSubmission.getScore());
                result.put("submissionTime", latestSubmission.getSubmittedAt());
                result.put("completed", true);
            } else {
                result.put("score", BigDecimal.ZERO);
                result.put("submissionTime", null);
                result.put("completed", false);
            }
            
        } catch (Exception e) {
            log.error("获取章节测验分数失败", e);
            result.put("success", false);
            result.put("message", "获取章节测验分数失败：" + e.getMessage());
        }
        
        return result;
    }

}
