package com.sky.allinone.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.UnknownHostException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import com.sky.allinone.dao.entity.GradeEvent;
import com.sky.allinone.service.RedisService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate; // springboot自动生成的，参见RedisAutoConfiguration
	@Autowired
	@Qualifier("redisTemplate")
	private RedisTemplate<Object, Object> redisTemplate; // springboot自动生成的，参见RedisAutoConfiguration
	@Autowired
	@Qualifier("redisTemplateJson")
	private RedisTemplate<Object, Object> myRedisTemplateJson; // 自定义的
	@Autowired
	private RedisTemplate<String, GradeEvent> myRedisTemplateGradeEvent; // 自定义的
	
	@Autowired
	private RedisConnectionFactory redisConnectionFactory; // 还有其他的factory，比如LettuceConnectionFactory
	
	/**
	 * 默认连的是本机的6379端口，密码在spring yml文件里配置的
	 */
	@Test
	public void testRedis() {
		redisTemplate.opsForValue().set("who", "sky");
		assertThat(redisTemplate.hasKey("who")).isTrue();
		assertThat(redisTemplate.opsForValue().get("who")).isEqualTo("sky");
		
		redisTemplate.delete("who");
		assertThat(redisTemplate.hasKey("who")).isFalse();
	}
	
	/**
	 * 测试redis的key和value的序列化和反序列化
	 * 各种序列化方式序列化后占用的空间不一样，可以考虑用一些好的序列化方式，比如google protobuf
	 * @throws UnknownHostException 
	 */
	@Test
	public void testSerializer() throws UnknownHostException {
		// 测试redisTemplate
		redisTemplate.opsForValue().set("you", "rock");
		/*
		 * 不知道为啥方法调用有二义性，换下面这种方式来调用
		 * Boolean exists = redisTemplate.execute(connection -> {
			return connection.exists("who".getBytes());
		});*/
		boolean exists = redisTemplate.execute(new RedisCallback<Boolean>() {

			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				//byte[] keyBytes = "who".getBytes(); // 不能这样直接获取key的bytes
				// RedisTemplate默认用的是JdkSerializationRedisSerializer
				RedisSerializer<String> keySerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
				byte[] keyBytes = keySerializer.serialize("you");
				
				return connection.exists(keyBytes);
			}
		});
		assertTrue(exists);
		
		// 测试RedisConnectionFactory和StringRedisTemplate的序列化机制
		RedisConnection connection = redisConnectionFactory.getConnection();
		connection.del(new JdkSerializationRedisSerializer().serialize("you")); // 需要专门的Serializer，不能使用StringRedisSerializer，否则会删不掉
		assertThat(stringRedisTemplate.hasKey("you")).isFalse(); // 不需要专门的Serializer
		
		GradeEvent event = new GradeEvent();
		event.setCategory("cate");
		event.setEventId("1");
		String key = "event";
		
		// 使用json序列化-非泛型-myRedisTemplateGradeEvent
		BoundValueOperations<String, GradeEvent> boundValueOps = myRedisTemplateGradeEvent.boundValueOps(key);
		boundValueOps.set(event);
		assertThat(redisTemplate.hasKey(key)).isFalse(); // 序列化机制不同
		assertThat(stringRedisTemplate.hasKey(key)).isTrue(); // 序列化机制相同
		event = boundValueOps.get();
		assertThat(event.getCategory()).isEqualTo("cate");
		stringRedisTemplate.delete(boundValueOps.getKey());
		assertThat(stringRedisTemplate.hasKey(key)).isFalse();
		
		// 使用json序列化-非泛型-强制转换-myRedisTemplateJson
		BoundValueOperations<Object, Object> bound1 = myRedisTemplateJson.boundValueOps(key);
		bound1.set(event);
		assertThat(redisTemplate.hasKey(key)).isFalse(); // 序列化机制不同
		assertThat(stringRedisTemplate.hasKey(key)).isTrue(); // 序列化机制相同
		event = (GradeEvent) bound1.get();
		assertThat(event.getCategory()).isEqualTo("cate");
		stringRedisTemplate.delete((String) bound1.getKey());
		assertThat(stringRedisTemplate.hasKey(key)).isFalse();
		
		// 使用json序列化-泛型
		RedisTemplate<String, GradeEvent> redisTemplateJson = redisService.getRedisTemplateJson(GradeEvent.class);
		BoundValueOperations<String, GradeEvent> bound2 = redisTemplateJson.boundValueOps(key);
		bound2.set(event);
		assertThat(redisTemplate.hasKey(key)).isFalse(); // 序列化机制不同
		assertThat(stringRedisTemplate.hasKey(key)).isTrue(); // 序列化机制相同
		event = bound2.get();
		assertThat(event.getCategory()).isEqualTo("cate");
		stringRedisTemplate.delete(bound2.getKey());
		assertThat(stringRedisTemplate.hasKey(key)).isFalse();
	}
	
	/**
	 * 还有很多功能没有测试，具体可以参看官方文档
	 * 比如：sentinel模式、集群模式、各种数据类型的测试、pipeline、lua、事务等
	 */
	@Test
	public void testTodo() {
		
	}
}
