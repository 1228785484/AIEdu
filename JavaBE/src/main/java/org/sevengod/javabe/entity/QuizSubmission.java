package org.sevengod.javabe.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("quiz_submissions")
public class QuizSubmission {
    @TableId(type = IdType.AUTO)
    private Long submissionId;
    private Long quizId;
    private Long userId;
    private String answers;  // JSON string
    private BigDecimal score;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime submittedAt;
    private String generatedQuestions;  // JSON string
}
