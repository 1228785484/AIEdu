package org.sevengod.javabe.entity.dto;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("courses")
public class Course {
    @TableId(type = IdType.AUTO)
    private Long courseId;
    private String title;
    private String description;
    private Long categoryId;
    private LocalDateTime createdAt;
    private Boolean isActive;
} 