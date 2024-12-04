package org.sevengod.javabe.tasks.jobs;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisGlobalBucketTask {
    
    @Autowired
    private RedissonClient redissonClient;

    private static final String BUCKET_KEY = "token_bucket";
    private static final String LAST_REFILL_TIME_KEY = "last_refill_time";
    private static final String BUCKET_LOCK_KEY = "token_bucket_lock";
    
    private static final int MAX_TOKENS = 100; // 最大令牌数
    private static final int TOKENS_PER_REFRESH = 10; // 每次添加的令牌数
    private static final long REFILL_INTERVAL = 1000; // 补充令牌的时间间隔（毫秒）
    private static final long LOCK_TIMEOUT = 10000; // 锁超时时间（毫秒）

    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void refreshTokenBucket() {
        RLock lock = redissonClient.getLock(BUCKET_LOCK_KEY);
        try {
            // 尝试获取分布式锁，防止多实例并发问题
            if (lock.tryLock(LOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    doRefresh();
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            log.error("服务器获取令牌桶锁失败", e);
            Thread.currentThread().interrupt();
        }
    }

    private void doRefresh() {
        // 初始化令牌桶
        RBucket<Integer> bucket = redissonClient.getBucket(BUCKET_KEY);
        RBucket<Long> lastRefillTime = redissonClient.getBucket(LAST_REFILL_TIME_KEY);
        //如果没找到Key
        if (!bucket.isExists()) {
            bucket.set(MAX_TOKENS);
            lastRefillTime.set(Instant.now().toEpochMilli());
            log.info("服务器全局令牌桶初始化完成，当前令牌数: {}", MAX_TOKENS);
            return;
        }

        // 计算需要补充的令牌数量
        long currentTime = Instant.now().toEpochMilli();
        long lastRefill = lastRefillTime.get();
        long timePassed = currentTime - lastRefill;
        
        if (timePassed >= REFILL_INTERVAL) {
            int currentTokens = bucket.get();
            // 计算需要添加的令牌数量
            int tokensToAdd = (int) (timePassed / REFILL_INTERVAL) * TOKENS_PER_REFRESH;
            // 确保不超过最大令牌数
            int newTokens = Math.min(currentTokens + tokensToAdd, MAX_TOKENS);
            
            bucket.set(newTokens);
            lastRefillTime.set(currentTime);
            
            log.info("服务器令牌已补充，当前令牌数: {}, 补充数量: {}", newTokens, newTokens - currentTokens);
        }
    }

    /**
     * 尝试获取指定数量的令牌
     * @param tokens 需要的令牌数量
     * @return 是否获取成功
     */
    public boolean tryAcquireTokens(int tokens) {
        RLock lock = redissonClient.getLock(BUCKET_LOCK_KEY);
        try {
            if (lock.tryLock(LOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
                try {
                    RBucket<Integer> bucket = redissonClient.getBucket(BUCKET_KEY);
                    Integer currentTokens = bucket.get();
                    
                    if (currentTokens == null || currentTokens < tokens) {
                        return false;
                    }
                    
                    bucket.set(currentTokens - tokens);
                    log.info("成功获取{}个令牌，剩余令牌数: {}", tokens, currentTokens - tokens);
                    return true;
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            log.error("获取令牌失败", e);
            Thread.currentThread().interrupt();
        }
        return false;
    }

    /**
     * 获取当前可用令牌数
     */
    public Integer getCurrentTokens() {
        RBucket<Integer> bucket = redissonClient.getBucket(BUCKET_KEY);
        return bucket.get();
    }

    /**
     * 获取令牌桶的使用率
     */
    public double getTokenBucketUsageRate() {
        Integer currentTokens = getCurrentTokens();
        if (currentTokens == null) {
            return 0.0;
        }
        return 1 - ((double) currentTokens / MAX_TOKENS);
    }
}