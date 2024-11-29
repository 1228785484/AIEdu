package org.sevengod.javabe.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "课程进度更新请求对象")
public class ProgressRequestVo {

    @Schema(description = "章节ID",
            example = "1",
            required = true)
    private Long chapterId;

    @Schema(description = "选课记录ID",
            example = "1",
            required = true)
    private Long enrollmentId;

    @Schema(description = "完成进度百分比",
            example = "75.5",
            minimum = "0",
            maximum = "100",
            required = true)
    private BigDecimal percentage;
}