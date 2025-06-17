package com.example.demo;

import lombok.extern.log4j.Log4j2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.demo.mapper")
@Log4j2
public class Demo1Application {

    public static void main(String[] args) {
        log.info("启动应用程序 Demo1Application");
        try {
            SpringApplication.run(Demo1Application.class, args);
            log.info("应用程序启动成功");
        } catch (Exception e) {
            log.error("应用程序启动失败: {}", e.getMessage(), e);
            throw e;
        }
    }
}
