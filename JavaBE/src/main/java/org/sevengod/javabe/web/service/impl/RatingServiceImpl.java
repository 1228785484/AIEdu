package org.sevengod.javabe.web.service.impl;

import com.esotericsoftware.minlog.Log;
import org.sevengod.javabe.entity.Chapter;
import org.sevengod.javabe.entity.Units;
import org.sevengod.javabe.util.DifyResponseUtil;
import org.sevengod.javabe.web.mapper.ChapterMapper;
import org.sevengod.javabe.web.mapper.UnitMapper;
import org.sevengod.javabe.web.service.DifyService;
import org.sevengod.javabe.web.service.QuizzesService;
import org.sevengod.javabe.web.service.RatingService;
import org.sevengod.javabe.web.service.StudyTimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@Service
public class RatingServiceImpl implements RatingService {
    private static final Logger log = LoggerFactory.getLogger(RatingServiceImpl.class);

    @Autowired
    private QuizzesService quizzesService;

    @Autowired
    private DifyService difyService;

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private StudyTimeService studyTimeService;

    @Value("${dify.key.rating-key}")
    private String APIKey;

    @Value("${dify.key.rating-key-overall}")
    private String APIKeyOverall;

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

    @Override
    public Map<String, Object> getOverallRating(Long userId, Long courseId) {
        log.info("开始生成总体评价 - 用户ID: {}, 课程ID: {}", userId, courseId);
        try {
            // 使用MyBatis Plus查询课程的所有单元
            List<Units> units = unitMapper.selectList(
                    new QueryWrapper<Units>()
                            .eq("course_id", courseId)
                            .orderByAsc("unit_order")
            );

            if (units.isEmpty()) {
                throw new IllegalArgumentException("课程不存在或没有单元: " + courseId);
            }

            // 使用MyBatis Plus查询所有章节
            List<Chapter> allChapters = chapterMapper.selectList(
                    new QueryWrapper<Chapter>()
                            .in("unit_id", units.stream()
                                    .map(Units::getUnitId)
                                    .collect(Collectors.toList()))
                            .orderByAsc("chapter_order")
            );

            // 按单元ID分组章节
            Map<Long, List<Chapter>> chaptersByUnit = allChapters.stream()
                    .collect(Collectors.groupingBy(Chapter::getUnitId));

            // 准备参数
            Map<String, String> params = new HashMap<>();

            // 为每个单元计算平均分
            for (int i = 0; i < Math.min(units.size(), 10); i++) {
                Units unit = units.get(i);
                List<Chapter> unitChapters = chaptersByUnit.getOrDefault(unit.getUnitId(), new ArrayList<>());
                List<Map<String, Object>> unitScores = unitChapters.stream()
                        .map(chapter -> quizzesService.getChapterQuizScores(chapter.getChapterId(), userId))
                        .toList();

                // 计算单元平均分
                double unitAverage = unitScores.stream()
                        .filter(score -> Boolean.TRUE.equals(score.get("hasQuiz")) && Boolean.TRUE.equals(score.get("completed")))
                        .mapToDouble(score -> Double.parseDouble(score.get("score").toString()))
                        .average()
                        .orElse(0.0);

                // 添加单元分数 (C1-C10)
                params.put("C" + (i + 1), String.format("%.2f", unitAverage));
            }

            // 获取课程总学习时长（秒）
            Long totalStudyTime = studyTimeService.getCourseStudyTimeSeconds(userId, courseId);

            params.put("Time", String.format("%d", totalStudyTime));

            // 调用DifyResponseUtil获取AI评价结果
            Map<String, Object> ratingResult = DifyResponseUtil.getAIResponse(
                    difyService,
                    params,
                    userId.toString(),
                    APIKeyOverall
            );

            log.info("成功生成总体评价 - 用户ID: {}", userId);
            return ratingResult;

        } catch (Exception e) {
            log.error("生成总体评价失败 - 用户ID: {}, 错误: {}", userId, e.getMessage());
            throw e;
        }
    }
}
