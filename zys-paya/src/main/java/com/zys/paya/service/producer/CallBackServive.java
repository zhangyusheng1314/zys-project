package com.zys.paya.service.producer;

import com.zys.paya.utils.FastJsonConvertUtil;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CallBackServive {
    public static final String CALLBACK_PAY_TOPIC = "callback_pay_topic";
    public static final String CALLBACK_PAY_TAGS = "callback_pay";




    private @Resource SyncProducer syncProducer;

    public void sendOKMessage(String orderId, String userId) {

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("orderId", orderId);
        params.put("status", "2");	//ok

        String keys = UUID.randomUUID().toString() + "$" + System.currentTimeMillis();
        Message message = new Message(CALLBACK_PAY_TOPIC, CALLBACK_PAY_TAGS, keys, FastJsonConvertUtil.convertObjectToJSON(params).getBytes());

        SendResult ret = syncProducer.sendMessage(message);
    }

}
