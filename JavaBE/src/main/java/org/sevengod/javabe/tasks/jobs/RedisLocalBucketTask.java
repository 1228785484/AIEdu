package org.sevengod.javabe.tasks.jobs;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisLocalBucketTask {
    private static final String BUCKET_KEY_FORMAT = "user:%s:rate_limit";
    private static final int MAX_TOKENS = 20;
    private static final int REPLENISH_TOKENS = 10;
    private static final int REPLENISH_PERIOD_MINUTES = 1;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 初始化用户令牌桶
     * @param userId user ID
     */
    public void initializeTokenBucket(String userId) {
        String bucketKey = String.format(BUCKET_KEY_FORMAT, userId);
        RBucket<Integer> bucket = redissonClient.getBucket(bucketKey);
        if (!bucket.isExists()) {
            bucket.set(MAX_TOKENS);
            bucket.expire(12, TimeUnit.HOURS); //对不活跃的用户12小时清除一次他们的Token
        }
    }

    /**
     * Try to consume a token from the bucket
     * @param userId 用户ID
     * @param tokens 消耗Token
     * @return true if 消耗成功, false 没token
     */
    public boolean tryConsume(String userId, int tokens) {
        String bucketKey = String.format(BUCKET_KEY_FORMAT, userId);
        RBucket<Integer> bucket = redissonClient.getBucket(bucketKey);
        
        if (!bucket.isExists()) {
            initializeTokenBucket(userId);
        }

        Integer currentTokens = bucket.get();
        if (currentTokens == null || currentTokens < tokens) {
            return false;
        }

        bucket.set(currentTokens - tokens);
        log.info("用户 {} 消耗 {} Token,用户剩余Token {}", userId, tokens,bucket.get());
        return true;
    }

    /**
     * 归还用户的令牌
     * @param userId 用户ID
     * @param tokens 归还的令牌数
     */
    public void returnTokens(String userId, int tokens) {
        String bucketKey = String.format(BUCKET_KEY_FORMAT, userId);
        RBucket<Integer> bucket = redissonClient.getBucket(bucketKey);
        
        if (!bucket.isExists()) {
            return; // 如果桶不存在，无需归还
        }

        Integer currentTokens = bucket.get();
        if (currentTokens != null) {
            int newTokens = Math.min(currentTokens + tokens, MAX_TOKENS);
            bucket.set(newTokens);
        }
    }

    /**
     * 每分钟增加Token
     */
    @Scheduled(fixedRate = 60000) //每分钟运行
    public void replenishTokens() {
        Iterable<String> keys = redissonClient.getKeys().getKeysByPattern("user:*:rate_limit");
        for (String key : keys) {
            RBucket<Integer> bucket = redissonClient.getBucket(key);
            Integer currentTokens = bucket.get();
            if (currentTokens != null) {
                int newTokens = Math.min(currentTokens + REPLENISH_TOKENS, MAX_TOKENS);
                bucket.set(newTokens);
            }
        }
        log.debug("所有用户的Token已补充!");
    }

    /**
     * 获取用户当前可用Token
     * @param userId user ID
     * @return 可用的Token, 或者MAX_TOKENS如果桶不存在的话
     */
    public int getAvailableTokens(String userId) {
        String bucketKey = String.format(BUCKET_KEY_FORMAT, userId);
        RBucket<Integer> bucket = redissonClient.getBucket(bucketKey);
        Integer tokens = bucket.get();
        return tokens != null ? tokens : MAX_TOKENS;
    }
}
