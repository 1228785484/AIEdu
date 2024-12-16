package org.sevengod.javabe.web.service;

import java.util.Map;

public interface LessonPlanService {
    /**
     * Generate lesson plan based on average study duration, average score and chapter information
     * @param averageStudyDuration Average study duration in minutes
     * @param averageScore Average student score
     * @param chapterId Chapter ID for querying chapter name
     * @return Generated lesson plan content
     */
    Map<String, Object> generateLessonPlan(Integer averageStudyDuration, Double averageScore, Long chapterId);
}
