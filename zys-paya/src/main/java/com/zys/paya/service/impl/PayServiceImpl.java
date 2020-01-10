package com.zys.paya.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zys.paya.dao.PayDao;
import com.zys.paya.model.CustomerAccount;
import com.zys.paya.service.PayService;
import com.zys.paya.service.producer.CallBackServive;
import com.zys.paya.service.producer.TransactionProducer;
import com.zys.paya.utils.FastJsonConvertUtil;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhangys
 * @since 2019-11-25
 */
@Service
public class PayServiceImpl extends ServiceImpl<PayDao, CustomerAccount> implements PayService {
    public static final String PAY_TOPIC = "pay_topic";
    public static final String PAY_TAGS = "pay";
    private @Resource PayDao payDao;

    private @Resource
    TransactionProducer transactionProducer;
    private @Resource
    CallBackServive callBackServive;
    @Override
    public String payment(String userId, String orderId, String accountId, Double money,String platformAccount) {
        String paymentRet = "";
        try {
            //1用token验证操作 （重复提单问题）
            BigDecimal payMoney = new BigDecimal(money);
            CustomerAccount customerAccount = payDao.selectById(accountId);
            Integer version = customerAccount.getVersion();
            BigDecimal oldMoney = customerAccount.getCurrentBalance();
            //如何保证一个账户同时操作的时候只能有一个人扣款成功
            // 业务角度  一个账户只允许一个人登录
            // 技术角度  reids分布式锁 数据库的乐观锁


            // 2谁获得分布式锁谁扣款 （避免多个人同时操作）
            // 3当分布式锁都获取失败的时候 交由数据库乐观锁保证一致性
            BigDecimal newMoney = oldMoney.subtract(payMoney);
            if (newMoney.doubleValue()>0) {
                //组装消息 执行本地事务
                String key = UUID.randomUUID().toString() + "$" + System.currentTimeMillis();
                Map<String,Object> map = new HashMap<>();
                map.put("userId",userId);
                map.put("orderId",orderId);
                map.put("accountId",accountId);
                map.put("money",money);
                map.put("platformAccount",platformAccount);
                Message message = new Message(PAY_TOPIC,PAY_TAGS,key, FastJsonConvertUtil.convertObjectToJSON(map).getBytes());
                map.put("newMoney",newMoney);
                map.put("version",version);

                //	同步阻塞
                CountDownLatch countDownLatch = new CountDownLatch(1);
                map.put("currentCountDown", countDownLatch);
                //生产者发送自定义方法
                TransactionSendResult sendResult = transactionProducer.sendMessage(message,map);
                countDownLatch.await();
                //扣款消息发送成功且事务已经提交成功
                if (sendResult.getSendStatus()== SendStatus.SEND_OK
                        && sendResult.getLocalTransactionState()== LocalTransactionState.COMMIT_MESSAGE) {
                    //回调订单模块 通知支付成功
                    callBackServive.sendOKMessage(orderId,userId);
                    paymentRet = "支付成功!";
                }else {
                    paymentRet = "支付失败!";
                }

            }else{
                paymentRet =  "余额不足";
            }
        }catch (Exception e){
            e.printStackTrace();
            paymentRet =  "支付失败";
        }
        return paymentRet;
    }

    @Override
    public Integer updateAccount(String accountId, BigDecimal money, Integer version, Date date) {
        return payDao.updateAccount(accountId,money,version,date);
    }
}
