package org.sevengod.javabe.web.service.impl;

import com.esotericsoftware.minlog.Log;
import org.sevengod.javabe.entity.Chapter;
import org.sevengod.javabe.util.DifyResponseUtil;
import org.sevengod.javabe.web.mapper.ChapterMapper;
import org.sevengod.javabe.web.service.DifyService;
import org.sevengod.javabe.web.service.QuizzesService;
import org.sevengod.javabe.web.service.RatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class RatingServiceImpl implements RatingService {
    private static final Logger log = LoggerFactory.getLogger(RatingServiceImpl.class);

    @Autowired
    private QuizzesService quizzesService;

    @Autowired
    private DifyService difyService;

    @Autowired
    private ChapterMapper chapterMapper;

    @Value("${dify.key.rating-key}")
    private String APIKey;

    @Override
    public Map<String, Object> getChapterRating(Long chapterId, Long userId) {
        log.info("开始生成章节评分 - 章节ID: {}, 用户ID: {}", chapterId, userId);
        try {
            // 获取章节信息
            Chapter chapter = chapterMapper.selectById(chapterId);
            if (chapter == null) {
                throw new IllegalArgumentException("章节不存在: " + chapterId);
            }

            // 获取测验分数
            Map<String, Object> quizScores = quizzesService.getChapterQuizScores(chapterId, userId);

            // 调用DifyResponseUtil获取AI评分结果
            Map<String, String> params = new HashMap<>();

            params.put("Chapter", chapter.getTitle());

            // 根据测验完成情况设置分数
            if (Boolean.TRUE.equals(quizScores.get("hasQuiz")) && Boolean.TRUE.equals(quizScores.get("completed"))) {
                params.put("Score", quizScores.get("score").toString());
            } else {
                params.put("Score", "未完成测验");
            }




            Map<String, Object> ratingResult = DifyResponseUtil.getAIResponse(
                difyService,
                params,
                userId.toString(),
                APIKey
            );
            log.info("成功生成章节评分 - 章节: {}", chapter.getTitle());
            return ratingResult;

        } catch (Exception e) {
            log.error("生成章节评分失败 - 章节ID: {}, 错误: {}", chapterId, e.getMessage());
            throw e;
        }
    }
}
