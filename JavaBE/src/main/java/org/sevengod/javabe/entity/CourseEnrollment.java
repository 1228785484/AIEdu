package org.sevengod.javabe.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("course_enrollments")
public class CourseEnrollment {
    @TableId(type = IdType.AUTO)
    private Long enrollmentId;
    private Long userId;
    private Long courseId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
    private String status;  // ongoing, completed, dropped
} 