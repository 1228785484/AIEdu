package org.sevengod.javabe.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sevengod.javabe.entity.Quizzes;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public interface QuizzesService extends IService<Quizzes> {
    // 获取单个测验并调用Dify生成响应
    CompletableFuture<Map<String, Object>> getQuizWithDifyResponse(Long chapterId, Long userId);
    Map<String, Object> submitAnswerAndScore(Long quizId, Long userId, String questions, String answers, BigDecimal score);
    // 获取用户在指定单元的所有章节测验分数
    Map<String, Object> getUnitQuizScores(Long unitId, Long userId);

}
