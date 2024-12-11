package org.sevengod.javabe.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("study_notes")
public class StudyNote {
    
    @TableId(value = "note_id", type = IdType.AUTO)
    private Long noteId;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("chapter_id")
    private Long chapterId;
    
    @TableField("content")
    private String content;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableField("is_private")
    private Boolean isPrivate;
    
    @TableField("is_active")
    private Boolean isActive;
    
    // 非数据库字段，用于展示章节信息
    @TableField(exist = false)
    private String chapterTitle;
    
    // 非数据库字段，用于展示用户信息
    @TableField(exist = false)
    private String username;
}