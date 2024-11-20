package org.sevengod.javabe.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 课程单元实体类
 */
@Data
@TableName("units")
public class Units {
    
    /**
     * 单元ID
     */
    @TableId(type = IdType.AUTO)
    private Long unitId;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 单元标题
     */
    private String title;
    
    /**
     * 单元描述
     */
    private String description;
    
    /**
     * 单元序号
     */
    private Integer sequenceNumber;
}
