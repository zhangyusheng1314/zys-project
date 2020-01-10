package com.zys.order.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zys.order.constants.OrderStatusEnum;
import com.zys.order.dao.OrderDao;
import com.zys.order.model.Order;
import com.zys.order.service.OrderService;
import com.zys.order.service.producer.PkgProducer;
import com.zys.order.utils.FastJsonConvertUtil;
import com.zys.store.service.api.StoreServiceApi;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhangys
 * @since 2019-11-25
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderDao, Order> implements OrderService {
    private @Resource OrderDao orderDao;
    private @Resource
    PkgProducer pkgProducer;
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    public static final String PKG_TOPIC = "pkg_topic";

    public static final String PKG_TAGS = "pkg";
    //对dubbo暴露接口的调用
    @Reference(
            version = "1.0.0",
            application = "${dubbo.application.id}",
            interfaceName = "com.zys.store.service.api.StoreServiceApi",
            check = false,
            timeout = 3000,
            retries = 0  //读请求3次 写请求不重复
    )
    private StoreServiceApi storeServiceApi;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean createOrder(String cityId, String platformId, String userId, String supplierId, String goodsId) {
        boolean flag = true;
        try {
            Order order = new Order();
            order.setOrderId(UUID.randomUUID().toString().substring(0, 32));
            order.setCityId(cityId);
            order.setPlatformId(platformId);
            order.setUserId(userId);
            order.setSupplierId(supplierId);
            order.setGoodsId(goodsId);
            order.setCreateBy("admin");
            order.setCreateTime(new Date());
            order.setOrderStatus(OrderStatusEnum.ORDER_SUCCESS.getStatus());
            order.setOrderType("1");
            order.setUpdateBy("admin");
            order.setUpdateTime(new Date());
            Integer version = storeServiceApi.selectVersion(supplierId,goodsId);
            Integer updateflag = storeServiceApi.updateVersionAndCount(version,"admin",new Date(),supplierId,goodsId);
            if (updateflag==1){
                orderDao.insert(order);
            }else{
                flag = false;
                Integer count = storeServiceApi.selectStoreCount(supplierId,goodsId);
                if (count==0){
                    System.err.println("-----当前库存不足...");
                }else{
                    System.err.println("-----乐观锁生效...");
                }
            }
        }catch (Exception e) {
            log.error("关联失败：{}",e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            // 	具体捕获的异常是什么异常
            flag = false;
        }

        return flag;
    }

    @Override
    public void sendOrderlyMessagePkg(String userId, String orderId) {
        List<Message> messageList = new ArrayList<>();

        Map<String, Object> param1 = new HashMap<String, Object>();
        param1.put("userId", userId);
        param1.put("orderId", orderId);
        param1.put("text", "创建包裹操作---1");

        String key1 = UUID.randomUUID().toString() + "$" +System.currentTimeMillis();
        Message message1 = new Message(PKG_TOPIC, PKG_TAGS, key1, FastJsonConvertUtil.convertObjectToJSON(param1).getBytes());

        messageList.add(message1);


        Map<String, Object> param2 = new HashMap<String, Object>();
        param2.put("userId", userId);
        param2.put("orderId", orderId);
        param2.put("text", "发送物流通知操作---2");

        String key2 = UUID.randomUUID().toString() + "$" +System.currentTimeMillis();
        Message message2 = new Message(PKG_TOPIC, PKG_TAGS, key2, FastJsonConvertUtil.convertObjectToJSON(param2).getBytes());

        messageList.add(message2);

        //	顺序消息投递 是应该按照 供应商ID 与topic 和 messagequeueId 进行绑定对应的
        //  supplier_id

        Order order = orderDao.selectById(orderId);
        int messageQueueNumber = Integer.parseInt(order.getSupplierId());

        //对应的顺序消息的生产者 把messageList 发出去通知物流模块
        pkgProducer.sendOrderlyMessage(messageList, messageQueueNumber);
    }
}
