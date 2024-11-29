package org.sevengod.javabe.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "课程报名请求对象", 
        title = "课程报名请求")
public class EnrollRequestVo {
    
    @Schema(description = "用户ID", 
            example = "1", 
            required = true,
            title = "报名学生的用户ID")
    private Long userId;
    
    @Schema(description = "课程ID", 
            example = "1", 
            required = true,
            title = "要报名的课程ID")
    private Long courseId;
}