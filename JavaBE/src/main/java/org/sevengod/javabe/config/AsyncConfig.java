package org.sevengod.javabe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AsyncConfig {
    
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 8核16线程的CPU，设置核心线程数为 9
        executor.setCorePoolSize(9);
        // 最大线程数设置为 16，对应CPU的逻辑处理器数
        executor.setMaxPoolSize(16);
        // 由于是IO密集型任务，队列容量可以适当增加
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("DifyThread-");
        // 设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
} 