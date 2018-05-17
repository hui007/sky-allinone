package com.sky.allinone.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import com.sky.allinone.service.RedisService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
	@Autowired
	private RedisService redisService;
	
	/**
	 * 默认连的是本机的6379端口，密码在spring yml文件里配置的
	 */
	@Test
	public void testRedis() {
		RedisTemplate<String, String> redisTemplate = redisService.getRedisTemplate();
		redisTemplate.opsForValue().set("who", "sky");
		assertThat(redisTemplate.hasKey("who")).isTrue();
		/*
		 * 不知道为啥方法调用有二义性，换下面这种方式来调用
		 * Boolean exists = redisTemplate.execute(connection -> {
			return connection.exists("who".getBytes());
		});*/
		boolean exists = redisTemplate.execute(new RedisCallback<Boolean>() {

			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				//byte[] keyBytes = "who".getBytes(); // 不能这样直接获取key的bytes
				RedisSerializer<String> keySerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
				byte[] keyBytes = keySerializer.serialize("who");
				
				return connection.exists(keyBytes);
			}
		});
		assertTrue(exists);
		assertThat(redisTemplate.opsForValue().get("who")).isEqualTo("sky");
		
		RedisConnectionFactory redisConnectionFactory = redisService.getRedisConnectionFactory();
		RedisConnection connection = redisConnectionFactory.getConnection();
		connection.del(new StringRedisSerializer().serialize("who")); // 需要专门的Serializer
		assertThat(redisService.getStringRedisTemplate().hasKey("who")).isFalse(); // 不需要专门的Serializer
	}
	
	/**
	 * 还有很多功能没有测试，具体可以参看官方文档
	 * 比如：sentinel模式、集群模式、各种数据类型的测试、pipeline、lua、事务等
	 */
	@Test
	public void testTodo() {
		
	}
}
