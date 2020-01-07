package com.zys.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zys.store.service.api.HelloServiceApi;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @Reference(
            version = "1.0.0",
            application = "${dubbo.application.id}",
            interfaceName = "com.zys.store.service.api.HelloServiceApi",
            check = false,
            timeout = 3000,
            retries = 0  //读请求3次 写请求不重复
    )
    private HelloServiceApi helloServiceApi;

    @RequestMapping("/hello")
    public String hello(@RequestParam("name") String name){
        return helloServiceApi.sayHello(name);
    }

}
