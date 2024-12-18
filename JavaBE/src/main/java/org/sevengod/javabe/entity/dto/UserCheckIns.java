package org.sevengod.javabe.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user_check_ins")
public class UserCheckIns {
    
    @TableId(value = "check_in_id", type = IdType.AUTO)
    private Long checkInId;
    
    private Long userId;
    
    private LocalDate checkInDate;
    
    private LocalDateTime checkInTime;
}
