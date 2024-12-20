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
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;

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
     * 检查用户是否完成了某章节的测验
     *
     * @param userId    用户ID
     * @param chapterId 章节ID
     * @return 如果测验已完成返回true
     */
    public boolean getQuizCompleteStatus(Long userId, Long chapterId) {
        // 首先获取章节的测验ID
        LambdaQueryWrapper<Quizzes> quizQuery = new LambdaQueryWrapper<>();
        quizQuery.eq(Quizzes::getChapterId, chapterId);
        List<Long> quizIds = quizzesMapper.selectList(quizQuery)
                .stream()
                .map(Quizzes::getQuizId)
                .toList();

        // 检查章节的测验提交情况
        if (!quizIds.isEmpty()) {
            LambdaQueryWrapper<QuizSubmission> quizWrapper = new LambdaQueryWrapper<>();
            quizWrapper.eq(QuizSubmission::getUserId, userId)
                    .in(QuizSubmission::getQuizId, quizIds);
            return quizSubmissionMapper.exists(quizWrapper);
        }
        return false;
    }

    /**
     * 检查用户是否完成了某章节的测验和个性化内容
     *
     * @param userId    用户ID
     * @param chapterId 章节ID
     * @return 如果测验和个性化内容都完成则返回true
     */
    public boolean getCompleteStatus(Long userId, Long chapterId) {
        // 检查测验完成情况
        boolean hasQuiz = getQuizCompleteStatus(userId, chapterId);

        // 检查个性化内容情况
        LambdaQueryWrapper<PersonalizedContents> contentWrapper = new LambdaQueryWrapper<>();
        contentWrapper.eq(PersonalizedContents::getUserId, userId)
                .eq(PersonalizedContents::getChapterId, chapterId)
                .eq(PersonalizedContents::getIsActive, true);
        boolean hasContent = personalizedContentsMapper.exists(contentWrapper);

        // 只有测验和个性化内容都完成才返回true
        return hasQuiz && hasContent;
    }

    /**
     * 获取单元内所有章节的完成统计信息
     *
     * @param userId 用户ID
     * @param unitId 单元ID
     * @return 包含总章节数和已完成章节数的Map
     */
    public Map<String, Integer> getUnitCompletionStats(Long userId, Long unitId) {
        Map<String, Integer> result = new HashMap<>();

        // 获取单元内所有章节
        LambdaQueryWrapper<Chapter> chapterQuery = new LambdaQueryWrapper<>();
        chapterQuery.eq(Chapter::getUnitId, unitId);
        List<Chapter> chapters = chapterMapper.selectList(chapterQuery);

        int totalChapters = chapters.size();
        int completedChapters = 0;

        // 检查每个章节的完成情况
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
     * 获取单元内所有章节的测验完成统计信息
     *
     * @param userId 用户ID
     * @param unitId 单元ID
     * @return 包含已完成测验数和总测验数的Map
     */
    public Map<String, Integer> getQuizCompletionStats(Long userId, Long unitId) {
        Map<String, Integer> result = new HashMap<>();

        // 获取单元内所有章节
        LambdaQueryWrapper<Chapter> chapterQuery = new LambdaQueryWrapper<>();
        chapterQuery.eq(Chapter::getUnitId, unitId);
        List<Chapter> chapters = chapterMapper.selectList(chapterQuery);

        // 获取章节的测验ID
        List<Long> chapterIds = chapters.stream()
                .map(Chapter::getChapterId)
                .toList();

        int totalQuizzes = 0;
        int completedQuizzes = 0;

        if (!chapterIds.isEmpty()) {
            // 获取总测验数
            LambdaQueryWrapper<Quizzes> quizQuery = new LambdaQueryWrapper<>();
            quizQuery.in(Quizzes::getChapterId, chapterIds);
            totalQuizzes = quizzesMapper.selectCount(quizQuery).intValue();

            // 计算完成的测验数
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
     * 获取用户课程的完成百分比
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return 包含完成百分比的Map（字符串格式）
     */
    public Map<String, String> getCourseCompletionPercentage(Long userId, Long courseId) {
        Map<String, String> result = new HashMap<>();

        // 获取课程内所有章节
        LambdaQueryWrapper<Chapter> chapterQuery = new LambdaQueryWrapper<>();
        chapterQuery.eq(Chapter::getCourseId, courseId);
        List<Chapter> chapters = chapterMapper.selectList(chapterQuery);

        int totalChapters = chapters.size();
        int completedChapters = 0;

        // 计算完成的章节数
        for (Chapter chapter : chapters) {
            if (getCompleteStatus(userId, chapter.getChapterId())) {
                completedChapters++;
            }
        }

        // 计算完成百分比（只取整数部分）
        int percentage = totalChapters > 0 ? (completedChapters * 100) / totalChapters : 0;
        result.put("completionPercentage", String.valueOf(percentage));

        return result;
    }

    /**
     * 更新用户完成章节的每日学习次数
     *
     * @param userId    用户ID
     * @param chapterId 章节ID
     * @return 如果学习次数更新成功返回true，如果章节未完成返回false
     */
    public boolean studyTimesUpdate(Long userId, Long chapterId) {
        // 首先检查章节是否完成
        if (!getCompleteStatus(userId, chapterId)) {
            return false;
        }

        // 每日计数器key格式: user:{userId}:study:daily
        String dailyKey = String.format("user:%d:study:daily", userId);
        
        // 每周计数器key格式: user:{userId}:study:weekly:{dayOfWeek}
        LocalDateTime now = LocalDateTime.now();
        String weeklyKey = String.format("user:%d:study:weekly:%d", userId, now.getDayOfWeek().getValue());

        // 使用原子操作更新每日和每周计数器
        RAtomicLong dailyCounter = redissonClient.getAtomicLong(dailyKey);
        RAtomicLong weeklyCounter = redissonClient.getAtomicLong(weeklyKey);

        // 递增两个计数器
        long dailyValue = dailyCounter.incrementAndGet();
        weeklyCounter.incrementAndGet();

        if (dailyValue == 1) {
            // 计算到今天结束的剩余时间
            LocalDateTime endOfDay = now.toLocalDate().plusDays(1).atStartOfDay();
            Duration timeUntilEndOfDay = Duration.between(now, endOfDay);
            
            // 设置每日计数器的过期时间
            dailyCounter.expire(timeUntilEndOfDay);
            
            // 设置每周计数器的过期时间
            // 计算到下周一的剩余天数
            int daysUntilNextWeek = 8 - now.getDayOfWeek().getValue(); // 使用8而不是7确保覆盖整个当天
            LocalDateTime endOfWeek = now.toLocalDate().plusDays(daysUntilNextWeek).atStartOfDay();
            Duration timeUntilEndOfWeek = Duration.between(now, endOfWeek);
            weeklyCounter.expire(timeUntilEndOfWeek);
        }

        return true;
    }

    /**
     * 获取用户今日的学习次数
     *
     * @param userId 用户ID
     * @return 今日完成的章节数
     */
    public int getStudyTimes(Long userId) {
        String key = String.format("user:%d:study:daily", userId);
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        return (int) atomicLong.get();
    }

    /**
     * 获取用户过去一周的学习次数
     *
     * @param userId 用户ID
     * @return 包含一周内每天学习次数的Map
     */
    public Map<String, Integer> getWeeklyStudyTimes(Long userId) {
        Map<String, Integer> weeklyStats = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        
        // 获取一周内每天的统计数据
        for (int i = 1; i <= 7; i++) {
            String key = String.format("user:%d:study:weekly:%d", userId, i);
            RAtomicLong counter = redissonClient.getAtomicLong(key);
            // 使用简单的数字格式作为key
            String dayName = String.valueOf(i);
            weeklyStats.put(dayName, (int) counter.get());
        }
        
        return weeklyStats;
    }

    /**
     * 获取用户本周的总学习次数
     *
     * @param userId 用户ID
     * @return 本周完成的总章节数
     */
    public int getTotalWeeklyStudyTimes(Long userId) {
        return getWeeklyStudyTimes(userId).values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    /**
     * 获取用户在指定课程中完成的总章节数
     * 
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return 用户在该课程中完成的总章节数
     */
    public Integer getCompletedChaptersCount(Long userId, Long courseId) {
        // 使用LambdaQueryWrapper查询指定课程的章节
        LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Chapter::getChapterId)
               .eq(Chapter::getCourseId, courseId);
        List<Chapter> courseChapters = chapterMapper.selectList(wrapper);
        
        if (courseChapters.isEmpty()) {
            return 0;
        }

        // 统计已完成的章节数
        int completedCount = 0;
        for (Chapter chapter : courseChapters) {
            if (getCompleteStatus(userId, chapter.getChapterId())) {
                completedCount++;
            }
        }
        
        return completedCount;
    }
}
