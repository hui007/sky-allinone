package com.sky.allinone.kafka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaSender {
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	public void sendMessage(String topic, String msg) {
		kafkaTemplate.send(topic, msg);
		kafkaTemplate.send(topic, "key1", msg);
	}
}
