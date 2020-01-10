package com.zys.order.service.consumer;

import com.zys.order.constants.OrderStatusEnum;
import com.zys.order.dao.OrderDao;
import com.zys.order.model.Order;
import com.zys.order.service.OrderService;
import com.zys.order.utils.FastJsonConvertUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 消费paya端支付成功后的回调消息通知已经支付成功
 */
@Component
public class OrderConsumer {
    private @Resource OrderDao orderDao;
    private @Resource OrderService orderService;
    public static final String CALLBACK_PAY_TOPIC = "callback_pay_topic";
    public static final String CALLBACK_PAY_TAGS = "callback_pay";

    private DefaultMQPushConsumer consumer;
    private static final String GROUP_NAME = "callback_consumer_group_name";
    private static final String NAME_SERVER = "192.168.146.128:9876;192.168.146.135:9876";

    public OrderConsumer() {

        try {
            this.consumer = new DefaultMQPushConsumer(GROUP_NAME);
            this.consumer.setConsumeThreadMin(10);
            this.consumer.setConsumeThreadMax(50);
            this.consumer.setNamesrvAddr(NAME_SERVER);
            this.consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            this.consumer.setMaxReconsumeTimes(3);
            this.consumer.subscribe(CALLBACK_PAY_TOPIC,CALLBACK_PAY_TAGS);
            this.consumer.setMessageListener(new OrderMessageListener());
            this.consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }


    }

    private class OrderMessageListener implements MessageListenerConcurrently {

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            MessageExt msg = msgs.get(0);

            try {
                String tags = msg.getTags();
                String topic = msg.getTopic();
                String keys = msg.getKeys();
                String msgBody = new String(msg.getBody(), "utf-8");
                System.err.println("收到消息：" + "  topic :" + topic + "  ,tags : " + tags + "keys :" + keys + ", msg : " + msgBody);
                String orignMsgId = msg.getProperties().get(MessageConst.PROPERTY_ORIGIN_MESSAGE_ID);
                System.err.println("orignMsgId: " + orignMsgId);
                Map<String, Object> body = FastJsonConvertUtil.convertJSONToObject(msgBody, Map.class);
                String orderId = (String) body.get("orderId");
                String userId = (String) body.get("userId");
                String status = (String)body.get("status");
                Date currentTime = new Date();

                if(status.equals(OrderStatusEnum.ORDER_PAYED.getStatus())) {
                    Order order = orderDao.selectById(orderId);
                    order.setOrderStatus(status);
                    order.setUpdateTime(currentTime);
                    int count = orderDao.updateById(order);
                    if(count == 1) {
                        orderService.sendOrderlyMessagePkg(userId, orderId);
                    }

                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }
}
