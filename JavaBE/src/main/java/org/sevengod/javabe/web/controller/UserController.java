package org.sevengod.javabe.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.common.AjaxResult;
import org.sevengod.javabe.entity.User;
import org.sevengod.javabe.web.service.UserService;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @Operation(summary = "获取用户列表", description = "分页获取用户列表，支持按用户名、邮箱和激活状态筛选")
    @GetMapping("/list")
    public AjaxResult getUserList(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize,
            
            @Parameter(description = "用户名（可选，用于模糊搜索）", example = "john")
            @RequestParam(required = false) String username,
            
            @Parameter(description = "邮箱（可选，用于模糊搜索）", example = "john@example.com")
            @RequestParam(required = false) String email,
            
            @Parameter(description = "是否激活（可选）", example = "true")
            @RequestParam(required = false) Boolean isActive
    ) {
        try {
            // 创建分页对象
            Page<User> page = new Page<>(pageNum, pageSize);
            
            // 调用服务层方法获取分页数据
            IPage<User> userPage = userService.getUserList(page, username, email, isActive);
            
            return AjaxResult.success("获取用户列表成功", userPage);
        } catch (Exception e) {
            return AjaxResult.error("获取用户列表失败：" + e.getMessage());
        }
    }
}
