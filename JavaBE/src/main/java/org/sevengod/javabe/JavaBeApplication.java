package org.sevengod.javabe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class JavaBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaBeApplication.class, args);
    }

}
