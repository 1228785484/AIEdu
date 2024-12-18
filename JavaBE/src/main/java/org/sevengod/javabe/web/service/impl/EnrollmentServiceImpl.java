package org.sevengod.javabe.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.sevengod.javabe.entity.dto.CourseEnrollment;
import org.sevengod.javabe.web.mapper.EnrollmentMapper;
import org.sevengod.javabe.web.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EnrollmentServiceImpl extends ServiceImpl<EnrollmentMapper, CourseEnrollment> implements EnrollmentService {
    @Autowired
    private EnrollmentMapper mapper;
    @Override
    public CourseEnrollment enrollCourse(Long userId, Long courseId) {
        // 检查是否已经选过这门课
        QueryWrapper<CourseEnrollment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("course_id", courseId);

        CourseEnrollment existingEnrollment = mapper.selectOne(queryWrapper);
        if (existingEnrollment != null) {
            if ("dropped".equals(existingEnrollment.getStatus())) {
                // 如果之前退课了，可以重新选课
                existingEnrollment.setStatus("ongoing");
                existingEnrollment.setEnrolledAt(LocalDateTime.now());
                existingEnrollment.setCompletedAt(null);
                mapper.updateById(existingEnrollment);
                return existingEnrollment;
            }
            throw new RuntimeException("您已经选过这门课程了");
        }

        // 创建新的选课记录
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setUserId(userId);
        enrollment.setCourseId(courseId);
        enrollment.setStatus("ongoing");
        enrollment.setEnrolledAt(LocalDateTime.now());

        mapper.insert(enrollment);
        return enrollment;
    }

    @Override
    public boolean isEnrolled(Long userId, Long courseId) {
        QueryWrapper<CourseEnrollment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("course_id", courseId)
                   .ne("status", "dropped");  // 排除已退课的情况
        
        return mapper.selectCount(queryWrapper) > 0;
    }
}
