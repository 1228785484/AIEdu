package org.sevengod.javabe.entity.dto;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_roles")
public class Roles {
    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;
    private Long userId;
    private String roleName;
    @Schema(description = "分配时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime assignedAt;
}
