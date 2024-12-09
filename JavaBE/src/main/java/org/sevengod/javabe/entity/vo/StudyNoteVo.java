package org.sevengod.javabe.entity.vo;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "学习笔记请求对象")
public class StudyNoteVo {
    
    @Schema(description = "笔记ID（更新时需要）")
    private Long noteId;
    
    @Schema(description = "章节ID")
    private Long chapterId;
    
    @Schema(description = "笔记内容")
    private String content;
    
    @Schema(description = "是否私密", defaultValue = "true")
    private Boolean isPrivate;
} 