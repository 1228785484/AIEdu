package org.sevengod.javabe.service;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
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
    private static final long KEY_EXPIRE_TIME = 1;

    private String getUserLockKey(String userId) {
        return "user:request.lock:" + userId;
    }

    public boolean tryLock(String userId) {
        String lockKey = getUserLockKey(userId);
        RBucket<String> bucket = redissonClient.getBucket(lockKey);
        if (!bucket.isExists()) {
            bucket.set(userId, KEY_EXPIRE_TIME, TimeUnit.HOURS);
            log.info("为用户 {} 创建锁key，过期时间为1小时", userId);
        }
        RLock lock = redissonClient.getLock(lockKey);
        try {
            // 尝试获取锁，如果获取不到就返回false
            boolean acquired = lock.tryLock(DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, TimeUnit.MILLISECONDS);
            if (acquired) {
                log.info("用户 {} 获取请求锁成功", userId);
            } else {
                log.info("用户 {} 已有正在处理的请求", userId);
            }
            return acquired;
        } catch (InterruptedException e) {
            log.error("获取用户请求锁时发生异常", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void unlock(String userId) {
        String lockKey = getUserLockKey(userId);
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
            log.info("用户 {} 释放请求锁", userId);
        }
    }
}