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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

	private StringRedisTemplate stringRedisTemplate;
	private RedisTemplate redisTemplate;
	private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate template) {
        this.stringRedisTemplate = template;
    }
    
    /**
     * 因为StringRedisTemplate也是RedisTemplate，不加@Qualifier会导致注入不了
     * @Qualifier("redisTemplate")是spring自动生成的RedisTemplate实例的名字
     * @param template
     */
    @Autowired
    public void setRedisTemplate(@Qualifier("redisTemplate")RedisTemplate template) {
    	this.redisTemplate = template;
    }
    
    @Autowired
    public void setRedisConnectionFactory(RedisConnectionFactory factory) {
    	this.redisConnectionFactory = factory;
    }

	public StringRedisTemplate getStringRedisTemplate() {
		return stringRedisTemplate;
	}

	public <k, v> RedisTemplate<k, v> getRedisTemplate() {
		return redisTemplate;
	}

	public RedisConnectionFactory getRedisConnectionFactory() {
		return redisConnectionFactory;
	}

}
