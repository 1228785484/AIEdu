package org.sevengod.javabe.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.entity.Chapter;
import org.sevengod.javabe.entity.Course;
import org.sevengod.javabe.entity.TreeNode;
import org.sevengod.javabe.entity.Units;
import org.sevengod.javabe.web.mapper.ChapterMapper;
import org.sevengod.javabe.web.mapper.CourseMapper;
import org.sevengod.javabe.web.mapper.UnitMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseTreeService {
    private final CourseMapper courseMapper;
    private final UnitMapper unitMapper;
    private final ChapterMapper chapterMapper;

    public List<TreeNode> buildCourseTree(Long courseId) {
        List<TreeNode> tree = new ArrayList<>();

        // 获取课程信息
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            return tree;
        }

        // 创建课程节点
        TreeNode courseNode = new TreeNode();
        courseNode.setId(course.getCourseId());
        courseNode.setName(course.getTitle());
        courseNode.setParentId(0L); // 课程是顶级节点

        // 获取课程下的所有单元
        List<Units> units = unitMapper.selectList(
            new QueryWrapper<Units>()
                .eq("course_id", courseId)
                .orderByAsc("sequence_number")
        );

        // 为每个单元创建节点并获取其章节
        for (Units unit : units) {
            TreeNode unitNode = new TreeNode();
            unitNode.setId(unit.getUnitId());
            unitNode.setName(unit.getTitle());
            unitNode.setParentId(course.getCourseId());

            // 获取单元下的所有章节
            List<Chapter> chapters = chapterMapper.selectList(
                new QueryWrapper<Chapter>()
                    .eq("unit_id", unit.getUnitId())
                    .orderByAsc("sequence_number")
            );

            // 为每个章节创建节点
            for (Chapter chapter : chapters) {
                TreeNode chapterNode = new TreeNode();
                chapterNode.setId(chapter.getChapterId());
                chapterNode.setName(chapter.getTitle());
                chapterNode.setParentId(unit.getUnitId());

                unitNode.getChildren().add(chapterNode);
            }

            courseNode.getChildren().add(unitNode);
        }

        tree.add(courseNode);
        return tree;
    }
}