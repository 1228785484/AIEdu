package org.sevengod.javabe.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.sevengod.javabe.entity.dto.User;
import org.sevengod.javabe.web.mapper.UserMapper;
import org.sevengod.javabe.web.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Override
    public User findByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
    }
    
    @Override
    public User findByEmail(String email) {
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
    }

    @Override
    public IPage<User> getUserList(Page<User> page, String username, String email, Boolean isActive) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件（如果提供了的话）
        queryWrapper
            .select(User.class, info -> !info.getColumn().equals("password_hash")) // 排除密码字段
            .like(StringUtils.isNotBlank(username), User::getUsername, username)
            .like(StringUtils.isNotBlank(email), User::getEmail, email)
            .eq(isActive != null, User::getIsActive, isActive)
            .orderByDesc(User::getCreatedAt);  // 按创建时间倒序排序
        
        return page(page, queryWrapper);
    }
}