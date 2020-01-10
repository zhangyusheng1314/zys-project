package com.zys.order.service.producer;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 下单成功后发送顺序消息给物流模块（必须按照消息顺序来消费消息）
 */
@Component
public class PkgProducer {
    private DefaultMQProducer defaultMQProducer;
    private static final String GROUP_NAME = "pkg_producer_group_name";
    private static final String NAME_SERVER = "192.168.146.128:9876;192.168.146.135:9876";
    public static final String PAY_TOPIC = "pkg_topic";

    public static final String PAY_TAGS = "pkg";

    public PkgProducer() {
        this.defaultMQProducer = new DefaultMQProducer(GROUP_NAME);
        this.defaultMQProducer.setNamesrvAddr(NAME_SERVER);
        this.defaultMQProducer.setRetryTimesWhenSendFailed(3);
        start();
    }

    public void sendOrderlyMessage(List<Message> msgs, int messageQueueNumber){
        for (Message msg : msgs) {
            try {
                this.defaultMQProducer.send(msg, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> mqs, Message message, Object o) {
                        Integer id = (Integer)o;
                        return mqs.get(id);
                    }
                },messageQueueNumber);
            } catch (MQClientException e) {
                e.printStackTrace();
            } catch (RemotingException e) {
                e.printStackTrace();
            } catch (MQBrokerException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void start(){
        try {
            this.defaultMQProducer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public void shutdown(){
        defaultMQProducer.shutdown();
    }
}
