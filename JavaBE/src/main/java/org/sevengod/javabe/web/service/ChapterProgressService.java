package org.sevengod.javabe.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sevengod.javabe.entity.ChapterProgress;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface ChapterProgressService extends IService<ChapterProgress> {
    public boolean updateProgress(Long chapterId, Long enrollmentId, BigDecimal percentage);
    public ChapterProgress getProgress(Long chapterId, Long enrollmentId);

}
