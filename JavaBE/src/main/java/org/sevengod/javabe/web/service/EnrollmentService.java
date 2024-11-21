package org.sevengod.javabe.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sevengod.javabe.entity.CourseEnrollment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface EnrollmentService extends IService<CourseEnrollment> {
    @Transactional
    public CourseEnrollment enrollCourse(Long userId, Long courseId);
}
