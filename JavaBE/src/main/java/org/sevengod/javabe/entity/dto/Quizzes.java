package org.sevengod.javabe.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("quizzes")
public class Quizzes {
    @TableId(type = IdType.AUTO)
    private Long quizId;

    private Long chapterId;

    private String title;

    private String quizPrompt;
}
