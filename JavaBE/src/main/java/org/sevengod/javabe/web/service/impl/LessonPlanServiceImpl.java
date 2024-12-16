package org.sevengod.javabe.web.service.impl;

import org.jetbrains.annotations.NotNull;
import org.sevengod.javabe.util.DifyResponseUtil;
import org.sevengod.javabe.entity.Chapter;
import org.sevengod.javabe.web.exception.DifyException;
import org.sevengod.javabe.web.mapper.ChapterMapper;
import org.sevengod.javabe.web.service.DifyService;
import org.sevengod.javabe.web.service.LessonPlanService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LessonPlanServiceImpl implements LessonPlanService {

    @Value("${dify.key.lesson-plan-key}")
    private String difyApiKey;

    @Value("${dify.url}")
    private String difyApiUrl;

    private final DifyService difyService;
    private final ChapterMapper chapterMapper;

    public LessonPlanServiceImpl(DifyService difyService, ChapterMapper chapterMapper) {
        this.difyService = difyService;
        this.chapterMapper = chapterMapper;
    }

    @Override
    public Map<String, Object> generateLessonPlan(Integer averageStudyDuration, Double averageScore, Long chapterId) {
        // Get chapter name by chapterId
        Chapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new RuntimeException("Chapter not found with id: " + chapterId);
        }
        Map<String, String> params = getStringStringMap(averageStudyDuration, averageScore, chapter);

        try {
            // Use DifyResponseUtil to get AI response
            Map<String, Object> response = DifyResponseUtil.getAIResponse(
                difyService,
                params,
                "teacher",
                difyApiKey
            );

            // Extract the lesson plan from response
            return response;
        } catch (DifyException e) {
            throw new RuntimeException("Failed to generate lesson plan: " + e.getMessage(), e);
        }
    }

    @NotNull
    private static Map<String, String> getStringStringMap(Integer averageStudyDuration, Double averageScore, Chapter chapter) {
        String chapterName = chapter.getTitle();

        // Prepare parameters for Dify API
        Map<String, String> params = new HashMap<>();

        params.put("Time", String.valueOf(averageStudyDuration));
        params.put("Score", String.valueOf(averageScore));
        params.put("Chapter", chapterName);
        return params;
    }
}
