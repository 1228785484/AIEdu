package org.sevengod.javabe;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAsync
@EnableScheduling  // 启用定时任务
@EnableAspectJAutoProxy // 启用AOP
@EnableCaching //启动缓存
// 确保扫描到所有需要的包
public class JavaBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaBeApplication.class, args);
        System.out.println("   _____                       _____           _ ");
        System.out.println("  / ____|                     / ____|         | |");
        System.out.println(" | (___   _____   _____ _ __ | |  __  ___   __| |");
        System.out.println("  \\___ \\ / _ \\ \\ / / _ \\ '_ \\| | |_ |/ _ \\ / _` |");
        System.out.println("  ____) |  __/\\ V /  __/ | | | |__| | (_) | (_| |");
        System.out.println(" |_____/ \\___| \\_/ \\___|_| |_|\\_____|\\___/ \\__,_|");
        System.out.println("——————————启动成功——————————");
    }

}
