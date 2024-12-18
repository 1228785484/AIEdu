package org.sevengod.javabe.web.service.impl;

import org.sevengod.javabe.entity.dto.Course;
import org.sevengod.javabe.dto.CourseBasicDTO;
import org.sevengod.javabe.web.mapper.CourseMapper;
import org.sevengod.javabe.web.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {
    
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<CourseBasicDTO> getAllCoursesBasic() {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_active", true)
                   .select("course_id", "title", "description")  // Only select required fields
                   .orderByAsc("created_at");
        
        return courseMapper.selectList(queryWrapper)
                .stream()
                .map(course -> {
                    CourseBasicDTO dto = new CourseBasicDTO();
                    dto.setCourseId(course.getCourseId());
                    dto.setTitle(course.getTitle());
                    dto.setDescription(course.getDescription());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
