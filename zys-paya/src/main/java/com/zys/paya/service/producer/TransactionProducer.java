package com.zys.paya.service.producer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.*;

/**
 * 分布式事务消息扣款 发送消息给payb模块 a账户扣款 b账户加钱
 */
@Component
public class TransactionProducer implements InitializingBean {
    private @Resource
    TransactionListenerImpl transactionListenerImpl;
    private TransactionMQProducer producer;
    private ExecutorService executorService;
    private static final String GROUP_NAME = "transaction_producer_group_name";
    private static final String NAME_SERVER = "192.168.146.128:9876;192.168.146.135:9876";

    public TransactionProducer() {
        this.producer = new TransactionMQProducer(GROUP_NAME);
        //线程池
        this.executorService = new ThreadPoolExecutor(2, 5, 200, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = newThread(r);
                thread.setName(GROUP_NAME+"-check-Thread");
                return thread;
            }
        });
        this.producer.setExecutorService(executorService);
        this.producer.setNamesrvAddr(NAME_SERVER);
    }
    //监听本地分布式事务
    @Override
    public void afterPropertiesSet() throws Exception {
        this.producer.setTransactionListener(transactionListenerImpl);
        start();
    }

    public TransactionSendResult sendMessage(Message message, Object args){
        TransactionSendResult sendResult = null;
        try {
            //发送事务消息
            sendResult = this.producer.sendMessageInTransaction(message,args);
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        return sendResult;
    }

    private void start() {
        try {
            this.producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }
    public void shutdown(){
        this.producer.shutdown();
    }
}
