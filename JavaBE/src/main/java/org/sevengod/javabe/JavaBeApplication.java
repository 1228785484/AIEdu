package org.sevengod.javabe;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
// 确保扫描到所有需要的包
public class JavaBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaBeApplication.class, args);
        System.out.println("——————————启动成功——————————");
    }

}
