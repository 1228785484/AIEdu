package org.sevengod.javabe.entity.dto;

import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "学习笔记返回对象")
public class StudyNoteDto {
    
    @Schema(description = "笔记ID")
    private Long noteId;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "章节ID")
    private Long chapterId;
    
    @Schema(description = "章节标题")
    private String chapterTitle;
    
    @Schema(description = "笔记内容")
    private String content;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
    
    @Schema(description = "是否私密")
    private Boolean isPrivate;
} 