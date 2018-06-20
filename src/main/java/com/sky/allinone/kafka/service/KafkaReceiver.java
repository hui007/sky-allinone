package com.sky.allinone.kafka.service;

import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaReceiver {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private final CountDownLatch latch1 = new CountDownLatch(1);
	
	@KafkaListener(id = "listener1", topics = "topic1")
    public void processMessage(ConsumerRecord<?, ?> cr) {
		logger.info("收到消息。key：{}，value：{}", cr.key(), cr.value());
		this.latch1.countDown();
    }
	
	public CountDownLatch getLatch1() {
		return latch1;
	}
}
