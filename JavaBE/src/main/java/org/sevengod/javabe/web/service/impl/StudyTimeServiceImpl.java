package org.sevengod.javabe.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.sevengod.javabe.entity.dto.DailyStudyTime;
import org.sevengod.javabe.web.mapper.DailyStudyTimeMapper;
import org.sevengod.javabe.web.service.StudyTimeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
public class StudyTimeServiceImpl extends ServiceImpl<DailyStudyTimeMapper, DailyStudyTime> implements StudyTimeService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDailyStudyTime(Long userId, Long courseId, LocalDate date, Long durationSeconds) {
        // 构建查询条件
        LambdaQueryWrapper<DailyStudyTime> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyStudyTime::getUserId, userId)
                .eq(DailyStudyTime::getCourseId, courseId)
                .eq(DailyStudyTime::getStudyDate, date);

        // 查询当天记录
        DailyStudyTime record = this.getOne(wrapper);

        if (record == null) {
            // 如果不存在，创建新记录
            record = new DailyStudyTime();
            record.setUserId(userId);
            record.setCourseId(courseId);
            record.setStudyDate(date);
            record.setDurationSeconds(durationSeconds);
            this.save(record);
        } else {
            // 如果存在，更新时长
            record.setDurationSeconds(record.getDurationSeconds() + durationSeconds);
            this.updateById(record);
        }

        log.info("Updated study time for user {} course {} on {}: {} seconds", 
                userId, courseId, date, record.getDurationSeconds());
    }

    @Override
    public Long getDailyStudyTimeSeconds(Long userId, Long courseId, LocalDate date) {
        LambdaQueryWrapper<DailyStudyTime> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyStudyTime::getUserId, userId)
                .eq(DailyStudyTime::getCourseId, courseId)
                .eq(DailyStudyTime::getStudyDate, date);

        DailyStudyTime record = this.getOne(wrapper);
        return record != null ? record.getDurationSeconds() : 0L;
    }

    @Override
    public Long getStudyTimeByRange(Long userId, Long courseId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<DailyStudyTime> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyStudyTime::getUserId, userId)
                .eq(DailyStudyTime::getCourseId, courseId)
                .between(DailyStudyTime::getStudyDate, startDate, endDate);

        return this.list(wrapper).stream()
                .mapToLong(DailyStudyTime::getDurationSeconds)
                .sum();
    }

    @Override
    public Long getCourseStudyTimeSeconds(Long userId, Long courseId) {
        LambdaQueryWrapper<DailyStudyTime> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyStudyTime::getUserId, userId)
                .eq(DailyStudyTime::getCourseId, courseId);

        return this.list(wrapper).stream()
                .mapToLong(DailyStudyTime::getDurationSeconds)
                .sum();
    }
}
