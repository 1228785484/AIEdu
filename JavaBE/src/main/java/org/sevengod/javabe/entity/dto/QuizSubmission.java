package org.sevengod.javabe.entity.dto;

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
    private String generatedQuestions;  // JSON string
    private BigDecimal score;
    private Integer timeLeft;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime submittedAt;
}
