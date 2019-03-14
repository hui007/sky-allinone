package com.sky.allinone.kafka;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.test.context.junit4.SpringRunner;

import com.sky.allinone.kafka.service.KafkaReceiver;
import com.sky.allinone.kafka.service.KafkaSender;

/**
 * 必须先在本机启动kafka，运行在9092端口
 * @author joshui
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
public class KafkaTest {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private String topic1 = "topic1";
	private Object group = "group1";
	private String kafkaServer = "localhost:9092";
	@Autowired
	private KafkaSender kafkaSender;
	@Autowired
	private KafkaReceiver kafkaReceiver;
	
	@Test(timeout = 1000 * 60)
	public void useJava() throws Exception {
		logger.info("开始测试useJava");
		
		// 消费者-ContainerProperties
		ContainerProperties containerProps = new ContainerProperties("topic1", "topic2");
		final CountDownLatch latch = new CountDownLatch(4);
		containerProps.setMessageListener(new MessageListener<Integer, String>() {

			@Override
			public void onMessage(ConsumerRecord<Integer, String> message) {
				// 第一次启动时，有时候消费不到。第一次时，正常应该是先启动消费者，再启动生产者
				logger.info("useJava收到消息: " + message);
				latch.countDown();
			}

		});
		
		// 消费者-KafkaMessageListenerContainer
		KafkaMessageListenerContainer<Integer, String> container = createContainer(containerProps);
		container.setBeanName("useJava");
		container.start();
		Thread.sleep(1000); // wait a bit for the container to start
		
		// 生产者-发送消息
		KafkaTemplate<Integer, String> template = createTemplate();
		template.setDefaultTopic(topic1 );
		template.sendDefault(0, "foo");
		template.sendDefault(2, "bar");
		template.sendDefault(0, "baz");
		template.sendDefault(2, "qux");
		template.flush();
		assertTrue(latch.await(60, TimeUnit.SECONDS));
		
		container.stop();
		logger.info("结束测试useJava");

	}
	
	@Test(timeout = 1000 * 60)
	public void useSpringboot() throws Exception {
		kafkaSender.sendMessage(topic1, "来自springboot");
		assertTrue(kafkaReceiver.getLatch1().await(60, TimeUnit.SECONDS));
	}
	
	/**
	 * 还要很多没有测试到，具体可参考官方文档，比如：
	 * 使用spring提供的kafka的测试框架；
	 * kafka事务
	 * @throws Exception
	 */
	@Test
	public void testTodo() throws Exception {
		
	}

	private KafkaMessageListenerContainer<Integer, String> createContainer(ContainerProperties containerProps) {
		Map<String, Object> props = consumerProps();
		DefaultKafkaConsumerFactory<Integer, String> cf = new DefaultKafkaConsumerFactory<Integer, String>(props);
		KafkaMessageListenerContainer<Integer, String> container = new KafkaMessageListenerContainer<>(cf,
				containerProps);
		return container;
	}

	private KafkaTemplate<Integer, String> createTemplate() {
		Map<String, Object> senderProps = senderProps();
		ProducerFactory<Integer, String> pf = new DefaultKafkaProducerFactory<Integer, String>(senderProps);
		KafkaTemplate<Integer, String> template = new KafkaTemplate<>(pf);
		return template;
	}

	private Map<String, Object> consumerProps() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, group );
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		return props;
	}

	private Map<String, Object> senderProps() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return props;
	}

}
