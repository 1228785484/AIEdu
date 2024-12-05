package org.sevengod.javabe.web.service;

import org.sevengod.javabe.dto.CourseBasicDTO;
import java.util.List;

public interface CourseService {
    /**
     * Get all courses basic information (id, title, and description)
     * @return List of course basic information
     */
    List<CourseBasicDTO> getAllCoursesBasic();
}
