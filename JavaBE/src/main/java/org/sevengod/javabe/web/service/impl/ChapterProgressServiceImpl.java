package org.sevengod.javabe.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.sevengod.javabe.entity.ChapterProgress;
import org.sevengod.javabe.web.mapper.ChapterProgressMapper;
import org.sevengod.javabe.web.service.ChapterProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ChapterProgressServiceImpl extends ServiceImpl<ChapterProgressMapper, ChapterProgress> implements ChapterProgressService {
    @Autowired
    private ChapterProgressMapper chapterProgressMapper;

    @Override
    public boolean updateProgress(Long chapterId, Long enrollmentId, BigDecimal percentage) {
        ChapterProgress progress = getProgress(chapterId,enrollmentId);
        if (progress == null) {
            // 创建新进度记录
            progress = new ChapterProgress();
            progress.setEnrollmentId(enrollmentId);
            progress.setChapterId(chapterId);
            progress.setStatus("not_started");
        }

        // 更新进度信息
        progress.setCompletionPercentage(percentage);
        progress.setLastAccessed(LocalDateTime.now());

        // 根据完成度更新状态
        if (percentage.compareTo(new BigDecimal("100")) >= 0) {
            progress.setStatus("completed");
        } else if (percentage.compareTo(BigDecimal.ZERO) > 0) {
            progress.setStatus("in_progress");
        }

        return this.saveOrUpdate(progress);
    }

    @Override
    public ChapterProgress getProgress(Long chapterId, Long enrollmentId) {
        QueryWrapper<ChapterProgress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enrollment_id", enrollmentId)
                   .eq("chapter_id", chapterId);
        return this.getOne(queryWrapper);
    }


}
