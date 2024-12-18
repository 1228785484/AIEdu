package org.sevengod.javabe.entity.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 章节进度实体类
 */
@Data
@TableName("chapter_progress")
public class ChapterProgress {
    
    /**
     * 进度ID
     */
    @TableId(type = IdType.AUTO)
    private Long progressId;
    
    /**
     * 报名ID
     */
    private Long enrollmentId;
    
    /**
     * 章节ID
     */
    private Long chapterId;
    
    /**
     * 完成百分比
     */
    private BigDecimal completionPercentage;
    
    /**
     * 状态(not_started:未开始,in_progress:进行中,completed:已完成)
     */
    private String status;
    
    /**
     * 最后访问时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime lastAccessed;
} 