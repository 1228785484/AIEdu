package org.sevengod.javabe.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sevengod.javabe.entity.Chapter;
import org.sevengod.javabe.entity.Course;
import org.sevengod.javabe.entity.TreeNode;
import org.sevengod.javabe.entity.Units;
import org.sevengod.javabe.web.mapper.ChapterMapper;
import org.sevengod.javabe.web.mapper.CourseMapper;
import org.sevengod.javabe.web.mapper.UnitMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//课程树以及各种课程的查询服务
//我忘了改成Impl格式的了
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseTreeService {
    private static final Long ROOT_PARENT_ID = 0L;
    private final CourseMapper courseMapper;
    private final UnitMapper unitMapper;
    private final ChapterMapper chapterMapper;

    @Cacheable(value = "courseTree", key = "#courseId", condition = "#courseId != null")
    public List<TreeNode> buildCourseTree(Long courseId) {
        List<TreeNode> tree = new ArrayList<>();

        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            log.warn("Course not found with ID: {}", courseId);
            return tree;
        }

        TreeNode courseNode = createCourseNode(course);
        addUnitsToTree(courseNode, courseId);

        tree.add(courseNode);
        return tree;
    }

    private TreeNode createCourseNode(Course course) {
        TreeNode courseNode = new TreeNode();
        courseNode.setId(course.getCourseId());
        courseNode.setName(course.getTitle());
        courseNode.setParentId(ROOT_PARENT_ID);
        return courseNode;
    }

    private void addUnitsToTree(TreeNode courseNode, Long courseId) {
        List<Units> units = unitMapper.selectList(
            new QueryWrapper<Units>()
                .eq("course_id", courseId)
                .orderByAsc("sequence_number")
        );

        for (Units unit : units) {
            TreeNode unitNode = createUnitNode(unit, courseNode.getId());
            addChaptersToUnit(unitNode, unit.getUnitId());
            courseNode.getChildren().add(unitNode);
        }
    }

    private TreeNode createUnitNode(Units unit, Long parentId) {
        TreeNode unitNode = new TreeNode();
        unitNode.setId(unit.getUnitId());
        unitNode.setName(unit.getTitle());
        unitNode.setParentId(parentId);
        return unitNode;
    }

    private void addChaptersToUnit(TreeNode unitNode, Long unitId) {
        List<Chapter> chapters = chapterMapper.selectList(
            new QueryWrapper<Chapter>()
                .eq("unit_id", unitId)
                .orderByAsc("sequence_number")
        );

        for (Chapter chapter : chapters) {
            TreeNode chapterNode = createChapterNode(chapter, unitNode.getId());
            unitNode.getChildren().add(chapterNode);
        }
    }

    private TreeNode createChapterNode(Chapter chapter, Long parentId) {
        TreeNode chapterNode = new TreeNode();
        chapterNode.setId(chapter.getChapterId());
        chapterNode.setName(chapter.getTitle());
        chapterNode.setParentId(parentId);
        return chapterNode;
    }

    public Chapter getChapterById(Long chapterId) {
        return chapterMapper.selectById(chapterId);
    }
}