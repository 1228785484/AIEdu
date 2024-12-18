package org.sevengod.javabe.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.sevengod.javabe.entity.dto.Course;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}
