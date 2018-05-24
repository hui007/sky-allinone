package com.sky.allinone.cache;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.cache.RedisCacheKey;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import com.sky.allinone.dao.entity.GradeEvent;
import com.sky.allinone.service.CacheService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheTest {
	@Autowired
	CacheService cacheService;
	/**
	 * cache使用的是默认RedisTemplate
	 */
	@Autowired
	@Qualifier("redisTemplate")
	private RedisTemplate<Object, Object> redisTemplate;
	
	@Test
	public void testCache() {
		// 自定义key
		cacheService.getfooStr();
		RedisCacheKey redisCacheKey = new RedisCacheKey("fooStr").usePrefix("foo:".getBytes())
				.withKeySerializer(new JdkSerializationRedisSerializer());
		Boolean exists = (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {

			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.exists(redisCacheKey.getKeyBytes());
			}
		}); // 使用这么复杂的查询方式也是不得已，因为cache框架生成key值的机制比较特殊
		assertThat(exists).isTrue();
		
		// 默认机制生成key
		cacheService.getJDKStr();
		final RedisCacheKey redisCacheKey2 = new RedisCacheKey(new SimpleKey()).usePrefix("bar:".getBytes())
				.withKeySerializer(new JdkSerializationRedisSerializer());
		exists = (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {

			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.exists(redisCacheKey2.getKeyBytes());
			}
		});
		assertThat(exists).isTrue();
		
		// 默认机制生成的key会相互覆盖
		cacheService.getJDKStr2();
		Set<byte[]> keys = (Set<byte[]>) redisTemplate.execute(new RedisCallback<Set<byte[]>>() {

			@Override
			public Set<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.keys("bar:*org.springframework.cache.interceptor.SimpleKey*".getBytes());
			}
		});
		assertThat(keys.size()).isEqualTo(1);
		
		// 默认机制，但是方法带参数
		cacheService.getJDKStr3("asd");
		keys = (Set<byte[]>) redisTemplate.execute(new RedisCallback<Set<byte[]>>() {

			@Override
			public Set<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.keys("bar:*asd".getBytes());
			}
		});
		assertThat(keys.size()).isEqualTo(1);
	}
	
	@Test
	public void testCachePut() {
		// 第一次调用
		GradeEvent event = new GradeEvent();
		event.setEventId("1");
		event.setCategory("c");
		event.setDate(new Date());
		cacheService.saveGradeEvent(event);
		
		RedisCacheKey redisCacheKey = new RedisCacheKey("1").usePrefix("foo:".getBytes())
				.withKeySerializer(new JdkSerializationRedisSerializer());
		
		event = redisTemplate.execute(new RedisCallback<GradeEvent>() {

			@Override
			public GradeEvent doInRedis(RedisConnection connection) throws DataAccessException {
				byte[] bs = connection.get(redisCacheKey.getKeyBytes());
				GradeEvent event = (GradeEvent) redisTemplate.getValueSerializer().deserialize(bs);
				
				return event;
			}
		});
		
		assertThat(event.getCategory()).isEqualTo("c");
		
		// 第二次调用。缓存里的值会被更新
		event.setCategory("new");
		cacheService.saveGradeEvent(event);
		
		event = redisTemplate.execute(new RedisCallback<GradeEvent>() {

			@Override
			public GradeEvent doInRedis(RedisConnection connection) throws DataAccessException {
				byte[] bs = connection.get(redisCacheKey.getKeyBytes());
				GradeEvent event = (GradeEvent) redisTemplate.getValueSerializer().deserialize(bs);
				
				return event;
			}
		});
		assertThat(event.getCategory()).isEqualTo("new");
	}
	
	@Test
	public void testUnless() {
		// 会缓存
		cacheService.testUnless("c");
		RedisCacheKey redisCacheKey = new RedisCacheKey("c").usePrefix("foo:".getBytes())
				.withKeySerializer(new JdkSerializationRedisSerializer());
		boolean exists = (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {

			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.exists(redisCacheKey.getKeyBytes());
			}
		});
		assertThat(exists).isTrue();
		
		// 不会缓存
		cacheService.testUnless("notCache");
		RedisCacheKey redisCacheKey2 = new RedisCacheKey("notCache").usePrefix("foo:".getBytes())
				.withKeySerializer(new JdkSerializationRedisSerializer());
		exists = (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {

			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.exists(redisCacheKey2.getKeyBytes());
			}
		});
		assertThat(exists).isFalse();
	}
	
	@Test
	public void testCondition() {
		// 启用缓存
		cacheService.testCondition("1");
		RedisCacheKey redisCacheKey = new RedisCacheKey("1").usePrefix("foo:".getBytes())
				.withKeySerializer(new JdkSerializationRedisSerializer());
		boolean exists = (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {

			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.exists(redisCacheKey.getKeyBytes());
			}
		});
		assertThat(exists).isTrue();
		
		// 不启用缓存
		cacheService.testCondition("2");
		RedisCacheKey redisCacheKey2 = new RedisCacheKey("2").usePrefix("foo:".getBytes())
				.withKeySerializer(new JdkSerializationRedisSerializer());
		exists = (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {

			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.exists(redisCacheKey2.getKeyBytes());
			}
		});
		assertThat(exists).isFalse();
	}
	
	@Test
	public void testCacheEvict() {
		// 放入缓存
		cacheService.testCondition("1");
		RedisCacheKey redisCacheKey = new RedisCacheKey("1").usePrefix("foo:".getBytes())
				.withKeySerializer(new JdkSerializationRedisSerializer());
		boolean exists = (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {

			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.exists(redisCacheKey.getKeyBytes());
			}
		});
		assertThat(exists).isTrue();
		
		// 从缓存清除
		cacheService.remove("1");
		RedisCacheKey redisCacheKey2 = new RedisCacheKey("1").usePrefix("foo:".getBytes())
				.withKeySerializer(new JdkSerializationRedisSerializer());
		exists = (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {

			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.exists(redisCacheKey2.getKeyBytes());
			}
		});
		assertThat(exists).isFalse();
	}
	
	/**
	 * 未测试的知识点：
	 * 1、将缓存注解放到接口类的方法上，将会自动缓存所有的接口实现类方法
	 * 2、使用@CacheConfig，定义类级别的注解配置
	 * 3、使用@Caching，组合多个缓存注解，可以一次性生成多个缓存条目
	 * 4、自定义缓存配置
	 * 自定义CacheManager
	 * 实现RedisCachePrefix，自定义缓存的前缀（默认是cacheName打头的）；
	 * 扩展CachingConfigurerSupport，自定义KeyGenerator、
	 * 5、自定义缓存注解
	 * 继承@Caching，可以将常用缓存配置汇聚到一起
	 */
	@Test
	public void testTodo() {
		
	}
	
}
