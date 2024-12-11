package org.sevengod.javabe.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.sevengod.javabe.common.AjaxResult;
import org.sevengod.javabe.entity.po.StudyNotePo;
import org.sevengod.javabe.entity.vo.StudyNoteVo;
import org.sevengod.javabe.web.service.StudyNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/study-notes")
@Tag(name = "学习笔记接口", description = "学习笔记的增删改查接口")
public class StudyNoteController {

    @Autowired
    private StudyNoteService studyNoteService;

    @PostMapping
    @Operation(summary = "创建笔记")
    public AjaxResult createNote(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @RequestBody StudyNotePo notePo) {
        try {
            return AjaxResult.success("创建笔记成功", 
                    studyNoteService.createNote(userId, notePo));
        } catch (Exception e) {
            return AjaxResult.error("创建笔记失败" + e.getMessage());
        }
    }

    @PostMapping("/update")
    @Operation(summary = "更新笔记")
    public AjaxResult updateNote(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @RequestBody StudyNoteVo noteVo) {
        try {
            return AjaxResult.success("更新笔记成功", 
                    studyNoteService.updateNote(userId, noteVo));
        } catch (Exception e) {
            return AjaxResult.error("更新笔记失败：" + e.getMessage());
        }
    }

    @PostMapping("/delete/{noteId}")
    @Operation(summary = "删除笔记")
    public AjaxResult deleteNote(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "笔记ID") @PathVariable Long noteId) {
        try {
            // 检查笔记是否属于用户
            if (!studyNoteService.isNoteOwner(userId, noteId)) {
                return AjaxResult.error("您没有权限删除此笔记");
            }
            if (studyNoteService.deleteNote(userId, noteId)) {
                return AjaxResult.success("删除笔记成功");
            }
            return AjaxResult.error("删除笔记失败");
        } catch (Exception e) {
            return AjaxResult.error("删除笔记失败：" + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户的所有笔记")
    public AjaxResult getUserNotes(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            return AjaxResult.success("获取笔记成功", 
                    studyNoteService.getUserNotes(userId));
        } catch (Exception e) {
            return AjaxResult.error("获取笔记失败：" + e.getMessage());
        }
    }

    @GetMapping("/chapter")
    @Operation(summary = "获取章节的笔记")
    public AjaxResult getChapterNotes(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "章节ID") @RequestParam Long chapterId) {
        try {
            return AjaxResult.success("获取章节笔记成功", 
                    studyNoteService.getChapterNotes(userId, chapterId));
        } catch (Exception e) {
            return AjaxResult.error("获取章节笔记失败：" + e.getMessage());
        }
    }
} 