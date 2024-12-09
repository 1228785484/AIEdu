package org.sevengod.javabe.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("daily_study_time")
public class DailyStudyTime {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long courseId;
    private LocalDate studyDate;
    private Long durationSeconds;  // 学习时长（秒）
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}