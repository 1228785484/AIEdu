package org.sevengod.javabe.web.service;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserRequestLockService {

    @Autowired
    private RedissonClient redissonClient;

    private static final long DEFAULT_WAIT_TIME = 5000; // 等待锁的最大时间（毫秒）
    private static final long DEFAULT_LEASE_TIME = 30000; // 持有锁的最大时间（毫秒）

    private String getUserLockKey(String userId) {
        return "user_request_lock:" + userId;
    }

    public boolean tryLock(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            log.warn("尝试获取锁时userId为空");
            return false;
        }

        String lockKey = getUserLockKey(userId);
        RLock lock = redissonClient.getLock(lockKey);
        //如果已经是锁上的
        if(lock.isLocked()){
            log.info("已拒绝用户 {} 的请求，原因:已上锁",userId);
            return false;
        }
        //不是的话就开始获取锁
        try {
            // 尝试获取锁，设置等待时间和持有时间
            boolean acquired = lock.tryLock(DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, TimeUnit.MILLISECONDS);
            if (acquired) {
                log.info("用户 {} 获取请求锁成功", userId);
            } else {
                log.info("用户 {} 已有正在处理的请求", userId);
            }
            return acquired;
        } catch (InterruptedException e) {
            log.error("获取用户 {} 请求锁时发生异常", userId, e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void unlock(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            log.warn("尝试释放锁时userId为空");
            return;
        }

        String lockKey = getUserLockKey(userId);
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("用户 {} 释放请求锁成功", userId);
            }
        } catch (Exception e) {
            log.error("释放用户 {} 请求锁时发生异常", userId, e);
        }
    }
}