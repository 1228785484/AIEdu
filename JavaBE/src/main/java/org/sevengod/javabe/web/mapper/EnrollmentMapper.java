package org.sevengod.javabe.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.sevengod.javabe.entity.CourseEnrollment;

@Mapper
public interface EnrollmentMapper extends BaseMapper<CourseEnrollment> {
}
