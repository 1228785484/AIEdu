package org.sevengod.javabe.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("personalized_contents")
public class PersonalizedContents {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long chapterId;
    private String content;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    private Boolean isActive;
}