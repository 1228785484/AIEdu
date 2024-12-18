package org.sevengod.javabe.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.common.AjaxResult;
import org.sevengod.javabe.entity.dto.Roles;
import org.sevengod.javabe.web.service.RolesService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色相关接口")
public class RolesController {
    
    private final RolesService rolesService;

    @PostMapping("/teacher/{userId}")
    @Operation(summary = "分配教师角色")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public AjaxResult teacherAssign(@PathVariable Long userId) {
        if (rolesService.assignTeacherRole(userId)) {
            return AjaxResult.success("教师角色分配成功");
        }
        return AjaxResult.error("教师角色分配失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取角色详情")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public AjaxResult getById(@PathVariable Long id) {
        Roles role = rolesService.getById(id);
        return role != null ? AjaxResult.success(role) : AjaxResult.error("角色不存在");
    }
}
