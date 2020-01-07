package com.zys.pkg.service.consumer;

import com.zys.pkg.dao.PackageDao;
import com.zys.pkg.utils.FastJsonConvertUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class PkgConsumer {
	private @Resource
	PackageDao packageDao;

	private DefaultMQPushConsumer consumer;
	
	private static final String NAMESERVER = "192.168.146.128:9876;192.168.146.135:9876";
	
	private static final String CONSUMER_GROUP_NAME = "pkg_consumer_group_name";
	
	public static final String PAY_TOPIC = "pkg_topic";
	
	public static final String PAY_TAGS = "pkg";

	public PkgConsumer() {

		try {
			this.consumer = new DefaultMQPushConsumer(CONSUMER_GROUP_NAME);
			this.consumer.setConsumeThreadMin(10);
			this.consumer.setConsumeThreadMax(30);
			this.consumer.setNamesrvAddr(NAMESERVER);
			this.consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
			this.consumer.subscribe(PAY_TOPIC,PAY_TAGS);
			this.consumer.setMaxReconsumeTimes(3);
			this.consumer.registerMessageListener(new PkgMessageListener());
			this.consumer.start();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
	}

	private class PkgMessageListener implements MessageListenerOrderly {

		Random random = new Random();
		@Override
		public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
			for(MessageExt msg: msgs) {
				try {
					String topic = msg.getTopic();
					String msgBody = new String(msg.getBody(), "utf-8");
					String tags = msg.getTags();
					String keys = msg.getKeys();
					System.err.println("收到消息：" + "  topic :" + topic + "  ,tags : " + tags + "keys :" + keys + ", msg : " + msgBody);

					Map<String, Object> body = FastJsonConvertUtil.convertJSONToObject(msgBody, Map.class);
					String orderId = (String) body.get("orderId");
					String userId = (String) body.get("userId");
					String text = (String)body.get("text");

					//	模拟实际的业务耗时操作
					//	PS: 创建包裹信息  、对物流的服务调用（异步调用）
					TimeUnit.SECONDS.sleep(random.nextInt(3) + 1);

					System.err.println("业务操作: " + text);

				} catch (Exception e) {
					e.printStackTrace();
					return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
				}
			}

			return ConsumeOrderlyStatus.SUCCESS;
		}
	}
}

