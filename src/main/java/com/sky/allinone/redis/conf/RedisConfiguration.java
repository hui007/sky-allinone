package com.sky.allinone.redis.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
public class RedisConfiguration {
	
	/**
	 * spring boot会自动配置StringRedisTemplate、RedisTemplate、RedisConnectionFactory
	 * 如果自己定义了相同类型的bean、或者相同名称的bean（针对RedisTemplate），则springboot的自动配置不会生效
	 * @return
	 */
//	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new JedisConnectionFactory();
	}
}
