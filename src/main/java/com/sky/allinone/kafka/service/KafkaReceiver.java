package com.sky.allinone.kafka.service;

import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class KafkaReceiver {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private final CountDownLatch latch1 = new CountDownLatch(1);
	
	/**
	 * 多线程消费：每个topic对应一个【预处理线程池，对应一个阻塞队列】，对应一个处理线程池
	 * worker：每个topic对应一个n个worker，worker的工作由相应的处理线程池调度
	 * offset：手动管理
	 * 重复消费：
	 * 	每个消息带全局唯一递增序号，如果前面的消息没消费，直接忽略收到的后面的消息。
	 * 	每个消息带唯一序号，这样不能忽略后面收到的消息，消息不保证消费次序，但是可以通过发送次序保证。
	 * 场景：
	 * 	reblance：sessionTimeout超时、poll-interval超时。如果不想新的消费者重头开始消费，可以使用seek方法定位一下offset
	 */
	
//	@KafkaListener(id = "listener1", topics = "topic1")
    public void processMessage(ConsumerRecord<?, ?> cr, Acknowledgment ack) {
		logger.info("收到消息。key：{}，value：{}", cr.key(), cr.value());
		
		/**
		 * 业务逻辑还没开始处理/处理了一半/处理完，ack之前，挂了
		 */
		ack.acknowledge();
		this.latch1.countDown();
    }
	
	public CountDownLatch getLatch1() {
		return latch1;
	}
}
