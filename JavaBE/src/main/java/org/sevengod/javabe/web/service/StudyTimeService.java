package org.sevengod.javabe.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sevengod.javabe.entity.DailyStudyTime;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface StudyTimeService extends IService<DailyStudyTime> {
    void updateDailyStudyTime(Long userId, Long courseId, LocalDate date, Long durationSeconds);
    Long getDailyStudyTimeSeconds(Long userId, Long courseId, LocalDate date);
    Long getStudyTimeByRange(Long userId, Long courseId, LocalDate startDate, LocalDate endDate);
    Long getCourseStudyTimeSeconds(Long userId, Long courseId);
}