package com.zys.paya.service.producer;

import com.zys.paya.service.PayService;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 分布式事务扣款消息监听
 */
@Component
public class TransactionListenerImpl implements TransactionListener {
    private @Resource
    PayService payService;

    /**
     * 执行本地事务
     * @param message
     * @param o
     * @return
     */
    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
        System.err.println("执行本地事务单元------------");
        CountDownLatch currentCountDown = null;
        Map<String ,Object> map = (Map<String, Object>) o;
        String accountId = (String)map.get("accountId");
        BigDecimal newMoney = (BigDecimal)map.get("newMoney");
        Integer version = (Integer)map.get("version");
        currentCountDown = (CountDownLatch)map.get("currentCountDown");
        try {
            Integer count = payService.updateAccount(accountId,newMoney,version,new Date());
            if (count ==1) {
                currentCountDown.countDown();
                return LocalTransactionState.COMMIT_MESSAGE;
            }else {
                currentCountDown.countDown();
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        }catch (Exception e){
            e.printStackTrace();
            currentCountDown.countDown();
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }

    }

    /**
     * 回调函数 用来检查mq是否与生产者保持连接
     * @param messageExt
     * @return
     */
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        return null;
    }
}
