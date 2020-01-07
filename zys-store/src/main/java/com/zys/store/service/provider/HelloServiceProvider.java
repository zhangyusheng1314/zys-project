package com.zys.store.service.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.zys.store.service.api.HelloServiceApi;
import org.springframework.stereotype.Component;
@Component
@Service(
        version = "1.0.0",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}"
)
public class HelloServiceProvider implements HelloServiceApi{

    @Override
    public String sayHello(String name) {
        return "hello" + name;
    }
}
