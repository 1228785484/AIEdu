package org.sevengod.javabe.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.sevengod.javabe.tasks.jobs.RedisBucketTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class DifyRateLimitAspect {

    @Autowired
    private RedisBucketTask redisBucketTask;

    private static final int TOKENS_PER_REQUEST = 1; // 每个请求消耗的令牌数
    private static final long MAX_WAIT_TIME = 30000; // 最大等待时间（毫秒）

    @Around("execution(* org.sevengod.javabe.web.service.DifyService.*(..))")
    public Object aroundDifyService(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < MAX_WAIT_TIME) {
            if (redisBucketTask.tryAcquireTokens(TOKENS_PER_REQUEST)) {
                try {
                    return joinPoint.proceed();
                } catch (Exception e) {
                    log.error("Dify service error", e);
                    throw e;
                }
            }
            // 如果没有获取到令牌，等待一段时间再试
            TimeUnit.MILLISECONDS.sleep(100);
        }
        
        // 如果超过最大等待时间仍未获取到令牌
        log.warn("Rate limit exceeded for Dify service");
        return createErrorResponse("请求太频繁，请稍后再试");
    }

    private Object createErrorResponse(String message) {
        return ResponseEntity.status(429)
                .body(Map.of(
                        "error", "Too Many Requests",
                        "message", message
                ));
    }
}
