/**
 *    Copyright 2010-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.sky.allinone.service;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
	// 还有其他的factory，比如LettuceConnectionFactory
	@Autowired
	private RedisConnectionFactory redisConnectionFactory;

	/**
	 * 正常是不能这么玩的，每次都new了一个新的对象，可以将这个写入一个工具类（使用类泛型），搞成单例模式
	 * 或者使用RedisTemplate<String, Object>强制转换的方式
	 * @return
	 * @throws UnknownHostException
	 */
	public <V> RedisTemplate<String, V> getRedisTemplateJson(Class<V> type)
					throws UnknownHostException {
		RedisTemplate<String, V> template = new RedisTemplate<String, V>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new Jackson2JsonRedisSerializer<V>(type));
		template.afterPropertiesSet(); // 这里需要手动调用这个方法，如果是容器管理的话，就不用手动调用
		
		return template;
	}
}
