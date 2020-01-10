package com.zys.payb.service.consumer;

import com.zys.payb.dao.PlatformAccountDao;
import com.zys.payb.model.PlatformAccount;
import com.zys.payb.service.PlatformAccountService;
import com.zys.payb.utils.FastJsonConvertUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 消费paya端来的事务扣款消息
 */
@Component
public class PayConsumer {
	private @Resource
	PlatformAccountDao platformAccountDao;

	private DefaultMQPushConsumer consumer;
	
	private static final String NAMESERVER = "192.168.146.128:9876;192.168.146.135:9876";
	
	private static final String CONSUMER_GROUP_NAME = "transaction_consumer_group_name";
	
	public static final String PAY_TOPIC = "pay_topic";
	
	public static final String PAY_TAGS = "pay";

	public PayConsumer() {

		try {
			this.consumer = new DefaultMQPushConsumer(CONSUMER_GROUP_NAME);
			this.consumer.setConsumeThreadMin(10);
			this.consumer.setConsumeThreadMax(30);
			this.consumer.setNamesrvAddr(NAMESERVER);
			this.consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
			this.consumer.subscribe(PAY_TOPIC,PAY_TAGS);
			this.consumer.setMaxReconsumeTimes(3);
			this.consumer.registerMessageListener(new PayMessageListener());
			this.consumer.start();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
	}

	private class PayMessageListener implements MessageListenerConcurrently{
		@Override
		public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
			MessageExt message = msgs.get(0);

			try {
				String topic = message.getTopic();
				String tags = message.getTags();
				String keys = message.getKeys();
				String body = new String(message.getBody(), RemotingHelper.DEFAULT_CHARSET);
				System.err.println("收到事务消息, topic: " + topic + ", tags: " + tags + ", keys: " + keys + ", body: " + body);
				Map<String,Object> map = FastJsonConvertUtil.convertJSONToObject(body, Map.class);
//				String userId = (String)map.get("userId");	// customer userId
//				String accountId = (String)map.get("accountId");	//customer accountId
//				String orderId = (String)map.get("orderId");	// 	统一的订单
				BigDecimal money = (BigDecimal)map.get("money");	//	当前的收益款
				String platformAccount =(String)map.get("platformAccount");
				PlatformAccount pa = platformAccountDao.selectById(platformAccount);
				//PlatformAccount pa = platformAccountDao.selectByPrimaryKey(platformAccount);	//	当前平台的一个账号
				//PlatformAccount pa = new PlatformAccount();
				//BigDecimal bigDecimal = new BigDecimal(100);
				pa.setCurrentBalance(pa.getCurrentBalance().add(money));
				Date currentTime = new Date();
				pa.setVersion(pa.getVersion()+1);
				pa.setDateTime(currentTime);
				pa.setUpdateTime(currentTime);
				pa.setAccountId(platformAccount);
				platformAccountDao.updateByPrimaryKeySelective(pa);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return ConsumeConcurrentlyStatus.RECONSUME_LATER;
			}

			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		}
	}
}

