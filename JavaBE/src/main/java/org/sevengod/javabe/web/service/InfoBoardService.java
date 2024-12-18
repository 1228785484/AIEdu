package org.sevengod.javabe.web.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.sevengod.javabe.entity.dto.Chapter;
import org.sevengod.javabe.entity.dto.PersonalizedContents;
import org.sevengod.javabe.entity.dto.QuizSubmission;
import org.sevengod.javabe.entity.dto.Quizzes;
import org.sevengod.javabe.web.mapper.ChapterMapper;
import org.sevengod.javabe.web.mapper.PersonalizedContentMapper;
import org.sevengod.javabe.web.mapper.QuizSubmissionMapper;
import org.sevengod.javabe.web.mapper.QuizzesMapper;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InfoBoardService {

    @Autowired
    private QuizSubmissionMapper quizSubmissionMapper;

    @Autowired
    private PersonalizedContentMapper personalizedContentsMapper;

    @Autowired
    private QuizzesMapper quizzesMapper;

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * Check if user has completed quiz for a chapter
     *
     * @param userId    user ID
     * @param chapterId chapter ID
     * @return true if quiz is completed
     */
    public boolean getQuizCompleteStatus(Long userId, Long chapterId) {
        // First get quiz ID for this chapter
        LambdaQueryWrapper<Quizzes> quizQuery = new LambdaQueryWrapper<>();
        quizQuery.eq(Quizzes::getChapterId, chapterId);
        List<Long> quizIds = quizzesMapper.selectList(quizQuery)
                .stream()
                .map(Quizzes::getQuizId)
                .toList();

        // Check quiz submissions for this chapter's quizzes
        if (!quizIds.isEmpty()) {
            LambdaQueryWrapper<QuizSubmission> quizWrapper = new LambdaQueryWrapper<>();
            quizWrapper.eq(QuizSubmission::getUserId, userId)
                    .in(QuizSubmission::getQuizId, quizIds);
            return quizSubmissionMapper.exists(quizWrapper);
        }
        return false;
    }

    /**
     * Check if user has completed both quiz and personalized content for a chapter
     *
     * @param userId    user ID
     * @param chapterId chapter ID
     * @return true if both quiz submission and personalized content exist
     */
    public boolean getCompleteStatus(Long userId, Long chapterId) {
        // Check quiz completion
        boolean hasQuiz = getQuizCompleteStatus(userId, chapterId);

        // Check personalized contents for this chapter
        LambdaQueryWrapper<PersonalizedContents> contentWrapper = new LambdaQueryWrapper<>();
        contentWrapper.eq(PersonalizedContents::getUserId, userId)
                .eq(PersonalizedContents::getChapterId, chapterId)
                .eq(PersonalizedContents::getIsActive, true);
        boolean hasContent = personalizedContentsMapper.exists(contentWrapper);

        // Return true only if both exist
        return hasQuiz && hasContent;
    }

    /**
     * Get completion statistics for all chapters under a unit
     *
     * @param userId user ID
     * @param unitId unit ID
     * @return Map containing total chapters and completed chapters count
     */
    public Map<String, Integer> getUnitCompletionStats(Long userId, Long unitId) {
        Map<String, Integer> result = new HashMap<>();

        // Get all chapters for this unit
        LambdaQueryWrapper<Chapter> chapterQuery = new LambdaQueryWrapper<>();
        chapterQuery.eq(Chapter::getUnitId, unitId);
        List<Chapter> chapters = chapterMapper.selectList(chapterQuery);

        int totalChapters = chapters.size();
        int completedChapters = 0;

        // Check completion status for each chapter
        for (Chapter chapter : chapters) {
            if (getCompleteStatus(userId, chapter.getChapterId())) {
                completedChapters++;
            }
        }

        result.put("completedChapters", completedChapters);
        result.put("totalChapters", totalChapters);

        return result;
    }

    /**
     * Get quiz completion statistics for all chapters under a unit
     *
     * @param userId user ID
     * @param unitId unit ID
     * @return Map containing completed quizzes and total quizzes count
     */
    public Map<String, Integer> getQuizCompletionStats(Long userId, Long unitId) {
        Map<String, Integer> result = new HashMap<>();

        // Get all chapters for this unit
        LambdaQueryWrapper<Chapter> chapterQuery = new LambdaQueryWrapper<>();
        chapterQuery.eq(Chapter::getUnitId, unitId);
        List<Chapter> chapters = chapterMapper.selectList(chapterQuery);

        // Get all quizzes for these chapters
        List<Long> chapterIds = chapters.stream()
                .map(Chapter::getChapterId)
                .toList();

        int totalQuizzes = 0;
        int completedQuizzes = 0;

        if (!chapterIds.isEmpty()) {
            // Get total quizzes
            LambdaQueryWrapper<Quizzes> quizQuery = new LambdaQueryWrapper<>();
            quizQuery.in(Quizzes::getChapterId, chapterIds);
            totalQuizzes = quizzesMapper.selectCount(quizQuery).intValue();

            // Count completed quizzes
            for (Chapter chapter : chapters) {
                if (getQuizCompleteStatus(userId, chapter.getChapterId())) {
                    completedQuizzes++;
                }
            }
        }

        result.put("completedQuizzes", completedQuizzes);
        result.put("totalQuizzes", totalQuizzes);

        return result;
    }

    /**
     * Get course completion percentage for a user
     *
     * @param userId   user ID
     * @param courseId course ID
     * @return Map containing completion percentage as a string
     */
    public Map<String, String> getCourseCompletionPercentage(Long userId, Long courseId) {
        Map<String, String> result = new HashMap<>();

        // Get all chapters for this course
        LambdaQueryWrapper<Chapter> chapterQuery = new LambdaQueryWrapper<>();
        chapterQuery.eq(Chapter::getCourseId, courseId);
        List<Chapter> chapters = chapterMapper.selectList(chapterQuery);

        int totalChapters = chapters.size();
        int completedChapters = 0;

        // Count completed chapters
        for (Chapter chapter : chapters) {
            if (getCompleteStatus(userId, chapter.getChapterId())) {
                completedChapters++;
            }
        }

        // Calculate percentage (integer part only)
        int percentage = totalChapters > 0 ? (completedChapters * 100) / totalChapters : 0;
        result.put("completionPercentage", String.valueOf(percentage));

        return result;
    }

    /**
     * Update daily study times for a user's chapter completion
     *
     * @param userId    user ID
     * @param chapterId chapter ID
     * @return true if the study time was incremented, false if chapter not completed
     */
    public boolean studyTimesUpdate(Long userId, Long chapterId) {
        // Check if chapter is completed first
        if (!getCompleteStatus(userId, chapterId)) {
            return false;
        }

        // Key format: study:daily:u:{userId}:count
        String key = String.format("study:daily:u:%d:count", userId);

        // Use atomic increment operation
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);

        // Increment and set expiration if it's the first time
        long currentValue = atomicLong.incrementAndGet();
        if (currentValue == 1) {
            // Set expiration to 24 hours from now
            atomicLong.expire(Duration.ofHours(24));
        }

        return true;
    }

    /**
     * Get user's total study times for today
     *
     * @param userId user ID
     * @return number of completed chapters today
     */
    public int getStudyTimes(Long userId) {
        String key = String.format("study:daily:u:%d:count", userId);

        // Get the atomic long, which returns 0 if key doesn't exist
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        return (int) atomicLong.get();
    }
}
