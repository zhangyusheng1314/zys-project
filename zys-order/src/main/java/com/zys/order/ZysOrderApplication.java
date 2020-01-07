package com.zys.order;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDubboConfig
@EnableTransactionManagement
@ComponentScan(basePackages = "com.zys.order.*")
public class ZysOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZysOrderApplication.class, args);
    }

}
