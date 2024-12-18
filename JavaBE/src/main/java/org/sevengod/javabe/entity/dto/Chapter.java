package org.sevengod.javabe.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("course_chapters")
public class Chapter {
    @TableId(type = IdType.AUTO)
    private Long chapterId;
    private Long courseId;
    private Long unitId;
    private String title;
    private Integer sequenceNumber;
    private String contentPrompt;
    private Integer totalMissionPoints;
} 