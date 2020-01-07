package com.zys.store;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDubbo
@ComponentScan(basePackages = "com.zys.store.*")
public class ZysStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZysStoreApplication.class, args);
    }

}
