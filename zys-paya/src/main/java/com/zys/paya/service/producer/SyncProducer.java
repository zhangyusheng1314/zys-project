package com.zys.paya.service.producer;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.stereotype.Component;

@Component
public class SyncProducer {
    private DefaultMQProducer defaultMQProducer;
    private static final String GROUP_NAME = "callback_producer_group_name";
    private static final String NAME_SERVER = "192.168.146.128:9876;192.168.146.135:9876";

    public SyncProducer() {
        this.defaultMQProducer = new DefaultMQProducer(GROUP_NAME);
        this.defaultMQProducer.setNamesrvAddr(NAME_SERVER);
        this.defaultMQProducer.setRetryTimesWhenSendFailed(3);
        start();
    }

    public SendResult sendMessage(Message message){
        SendResult sendResult = null;
        try {
            sendResult = this.defaultMQProducer.send(message);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return sendResult;
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
