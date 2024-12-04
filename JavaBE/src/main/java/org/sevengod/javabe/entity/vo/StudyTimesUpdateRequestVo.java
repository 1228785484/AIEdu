package org.sevengod.javabe.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新学习次数请求")
public class StudyTimesUpdateRequestVo {
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "章节ID", example = "1")
    private Long chapterId;
}
