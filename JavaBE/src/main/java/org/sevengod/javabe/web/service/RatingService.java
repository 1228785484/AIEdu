package org.sevengod.javabe.web.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface RatingService {
    /**
     * 获取章节评分
     * @param chapterId 章节ID
     * @param userId 用户ID
     * @return 评分结果
     */
    Map<String, Object> getChapterRating(Long chapterId, Long userId);

    /**
     * 获取总体评价
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 总体评价结果
     */
    Map<String, Object> getOverallRating(Long userId, Long courseId);
}
