package org.sevengod.javabe.web.controller;

import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.entity.TreeNode;
import org.sevengod.javabe.entity.Units;
import org.sevengod.javabe.web.service.CourseTreeService;
import org.sevengod.javabe.web.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course")
public class CourseController {
    @Autowired
    private UnitService unitService;
    @Autowired
    private CourseTreeService courseTreeService;

    @GetMapping("/findUnits")
    public List<Units> findAll(@RequestParam int courseId) {
        return unitService.findAll(courseId);
    }
    @GetMapping("/{courseId}/tree")
    public ResponseEntity<List<TreeNode>> getCourseTree(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseTreeService.buildCourseTree(courseId));
    }
}
