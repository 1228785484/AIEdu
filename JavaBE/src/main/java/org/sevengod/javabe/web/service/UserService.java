package org.sevengod.javabe.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sevengod.javabe.entity.dto.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends IService<User> {
    User findByUsername(String username);
    User findByEmail(String email);
    
    /**
     * 获取用户列表（分页）
     *
     * @param page 分页参数
     * @param username 用户名（可选，用于模糊搜索）
     * @param email 邮箱（可选，用于模糊搜索）
     * @param isActive 是否激活（可选）
     * @return 分页后的用户列表
     */
    IPage<User> getUserList(Page<User> page, String username, String email, Boolean isActive);
}