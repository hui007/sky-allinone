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

import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.sky.allinone.dao.entity.GradeEvent;

@Service
public class CacheService {
	/*
	 * Note：
	 * 如果没有自定义redisTemplate，会使用默认的redisTemplate，使用jdk自带的序列化方式序列化key和value值，可读性很差
	 */
	
	/**
	 * 如果不指定key，cache的切面会使用SimpleKeyGenerator生成key，产生的缓存key可读性很差
	 * @return
	 */
	@Cacheable(cacheNames = "bar")
	public String getJDKStr() {
		return RandomStringUtils.random(5, "1234567890abcdef");
	}
	
	/**
	 * 如果不指定key，cache的切面会使用SimpleKeyGenerator生成key，并且会覆盖前一个SimpleKeyGenerator生成的key对应的值
	 * @return
	 */
	@Cacheable(cacheNames = "bar")
	public String getJDKStr2() {
		return RandomStringUtils.random(5, "1234567890abcdef");
	}
	
	/**
	 * 如果不指定key，cache的切面会使用SimpleKeyGenerator生成key，因为方法有参数，会导致和无参数的方法生成的key值不一样。
	 * 自动生成key值的类，基本就是基于方法参数来生成的
	 * @param str
	 * @return
	 */
	@Cacheable(cacheNames = "bar")
	public String getJDKStr3(String str) {
		return RandomStringUtils.random(5, "1234567890abcdef");
	}
	
	/**
	 * 自定义key为fooStr
	 * @return
	 */
	@Cacheable(cacheNames = "foo", key = "\"fooStr\"")
	public String getfooStr() {
		return RandomStringUtils.random(5, "1234567890abcdef");
	}
	
	/**
	 * 这个注解不会阻止方法的执行，可以用来预先加载缓存
	 * @param event
	 * @return
	 */
	@CachePut(cacheNames = "foo", key = "#result.eventId")
	public GradeEvent saveGradeEvent(GradeEvent event) {
		return event;
	}
	
	/**
	 * 当unless为true，会阻止返回结果放入缓存
	 * Cacheable注解里不能使用#result，因为在进入方法时，#result还没有生成
	 * @param category
	 * @return
	 */
	@Cacheable(cacheNames = "foo", key = "#category", unless = "#result.category.contains('notCache')")
	public GradeEvent testUnless(String category) {
		GradeEvent event = new GradeEvent();
		event.setEventId("2");
		event.setCategory(category);
		event.setDate(new Date());
		
		return event;
	}
	
	/**
	 * condition为true，才会启用缓存。否则不会查询缓存，也不会将返回结果放入缓存
	 * Cacheable注解里不能使用#result，因为在进入方法时，#result还没有生成
	 * @param eventId
	 * @return
	 */
	@Cacheable(cacheNames = "foo", key = "#a0", condition = "#p0.startsWith('1')")
	public GradeEvent testCondition(String eventId) {
		GradeEvent event = new GradeEvent();
		event.setEventId(eventId);
		event.setCategory("c");
		event.setDate(new Date());
		
		return event;
	}
	
	/**
	 * 清除缓存
	 * CacheEvict可以用在void方法上，@Cacheable和@CachePut只能用在有返回值的方法上
	 * @param eventId
	 */
	@CacheEvict(allEntries = false, beforeInvocation = false, cacheNames = "foo", condition = "", key = "#a0")
	public void remove(String eventId) {
		
	}
}
