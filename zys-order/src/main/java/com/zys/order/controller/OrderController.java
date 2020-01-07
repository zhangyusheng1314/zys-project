package com.zys.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.zys.order.service.OrderService;
import com.zys.store.service.api.StoreServiceApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

@RestController
public class OrderController {
    private @Resource
    OrderService orderService;
    //	超时降级
//	@HystrixCommand(
//        commandKey = "createOrder",
//        commandProperties = {
//                //开启超时降级策略
//                @HystrixProperty(name="execution.timeout.enabled", value="true"),
//                //设置超时时间 超过三千毫秒执行降级后的方法
//                @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="3000"),
//        },
//        //超时后降级的方法
//        fallbackMethod = "createOrderFallbackMethod4Timeout"
//    )
    //	限流策略：线程池方式
//	@HystrixCommand(
//        commandKey = "createOrder",
//        commandProperties = {
//                @HystrixProperty(name="execution.isolation.strategy", value="THREAD")
//        },
//        threadPoolKey = "createOrderThreadPool",
//        threadPoolProperties = {
//                //核心线程数
//                @HystrixProperty(name="coreSize", value="10"),
//                //最大队列值
//                @HystrixProperty(name="maxQueueSize", value="20000"),
//                //超过20个的队列外的请求拒绝
//                @HystrixProperty(name="queueSizeRejectionThreshold", value="30")
//        },
//        fallbackMethod="createOrderFallbackMethod4Thread"
//    )
    //	限流策略：信号量方式
    @HystrixCommand(
        commandKey="createOrder",
        commandProperties= {
                @HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE"),
                //最大请求数
                @HystrixProperty(name="execution.isolation.semaphore.maxConcurrentRequests", value="3")
        },
        fallbackMethod = "createOrderFallbackMethod4semaphore"
    )
    @RequestMapping("/createOrder")
    public String createOrder(@RequestParam("cityId")String cityId,
                              @RequestParam("platformId")String platformId,
                              @RequestParam("userId")String userId,
                              @RequestParam("supplierId")String supplierId,
                              @RequestParam("goodsId")String goodsId) throws Exception{

	    return orderService.createOrder(cityId,platformId,userId,supplierId,goodsId)?"下单成功":"下单失败";
    }

    public String createOrderFallbackMethod4Timeout(@RequestParam("cityId")String cityId,
                              @RequestParam("platformId")String platformId,
                              @RequestParam("userId")String userId,
                              @RequestParam("supplierId")String supplierId,
                              @RequestParam("goodsId")String goodsId) throws Exception{
        System.err.println("-------超时降级策略执行------------");
        return "hystrix timeout !";
    }

    public String createOrderFallbackMethod4Thread(@RequestParam("cityId")String cityId,
                                                   @RequestParam("platformId")String platformId,
                                                   @RequestParam("userId")String userId,
                                                   @RequestParam("suppliedId")String suppliedId,
                                                   @RequestParam("goodsId")String goodsId) throws Exception {
        System.err.println("-------线程池限流降级策略执行------------");
        return "hystrix threadPool !";
    }

    public String createOrderFallbackMethod4semaphore(@RequestParam("cityId")String cityId,
                                                      @RequestParam("platformId")String platformId,
                                                      @RequestParam("userId")String userId,
                                                      @RequestParam("suppliedId")String suppliedId,
                                                      @RequestParam("goodsId")String goodsId) throws Exception {
        System.err.println("-------信号量限流降级策略执行------------");
        return "hystrix semaphore !";
    }




    //批量请求合并
//    @HystrixCollapser(
//        batchMethod = "findAll",
//        collapserProperties = {
//            //单个请求延长时间
//            @HystrixProperty(name="timeoutInMilliseconds",value = "200"),
//            //满50个请求合并发送
//            @HystrixProperty(name="maxRequestsInBatch",value = "50"),
//            //是否开启本地缓存
//            @HystrixProperty(name="requestCache.enabled",value = "false")
//        }
//    )
//    public Future<String> find(long id){
//        return null;
//    }
//
//    @HystrixCommand
//    public List<String> findAll(List<Long> ids){
//        System.out.println("发送请求。。。参数为："+ids.toString()+Thread.currentThread().getName());
//        String[] result = restTemplate.getForEntity("http://HELLO-SERVICE/hjcs?ids={1}",String[].class, StringUtils.join(ids,",")).getBody();
//        return Arrays.asList(result);
//        return null;
//    }



}
