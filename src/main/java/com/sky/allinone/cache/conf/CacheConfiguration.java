package com.sky.allinone.cache.conf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 
 * @author joshui
 *
 */
@Configuration
@EnableCaching // 后台会创建一个切面
public class CacheConfiguration {
	
	/**
	 * spring集成了很多缓存管理器，测试的话，可以使用ConcurrentMapCacheManager这种
	 * 参见springboot官方文档，springboot会按照一定次序自动发现自动生成某一种CacheManager；也可以通过配置属性，告诉springboot初始化特定的CacheManager
	 * 
	 * 如果要组合多种缓存策略，可以使用CompositeCacheManager
	 * @return
	 */
//	@Bean
	public CacheManager cacheManager() {
		// 会使用ConcurrentHashMap作为缓存存储
		CacheManager concurrentMapCacheManager = new ConcurrentMapCacheManager();
		
		CompositeCacheManager managers = new CompositeCacheManager();
		managers.setCacheManagers(Arrays.asList(concurrentMapCacheManager));
		
		return managers;
	}
	
	/**
	 * 这种是springboot提供的类似回调方法的机制。
	 * 假设自动生成了ConcurrentMapCacheManager，在ConcurrentMapCacheManager能使用之前，可以通过这个方法定制它。
	 * @return
	 */
	@Bean
	public CacheManagerCustomizer<ConcurrentMapCacheManager> cacheManagerCustomizerConcurrentMapCacheManager() {
	    return new CacheManagerCustomizer<ConcurrentMapCacheManager>() {
	        @Override
	        public void customize(ConcurrentMapCacheManager cacheManager) {
	            cacheManager.setAllowNullValues(false);
	        }
	    };
	}
	
	/**
	 * 同cacheManagerCustomizerConcurrentMapCacheManager方法。
	 * 会自动使用bean name为“redisTemplate”的redisTemplate
	 * @return
	 */
	@Bean
	public CacheManagerCustomizer<RedisCacheManager> cacheManagerCustomizerRedisCacheManager() {
		return new CacheManagerCustomizer<RedisCacheManager>() {
			/* 
			 * 默认使用的是自动生成的RedisTemplate<Object, Object>，使用的序列化为jdk的，
			 * 缓存后，如果在命令行查看的话，看到的是乱码，因为使用的序列化机制不一样
			 * @see org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer#customize(org.springframework.cache.CacheManager)
			 */
			@Override
			public void customize(RedisCacheManager cacheManager) {
				// 这里定义一些全局信息
				
				// 是否跟事务关联：事务提交之后才提交
				cacheManager.setTransactionAware(true);
				
				// 设置过期时间。正常应该是在yml配置文件里定义
				cacheManager.setCacheNames(Arrays.asList("foo", "bar"));
				Map<String, Long> exps = new HashMap<String, Long>();
				exps.put("foo", Long.valueOf(60 * 5));
				exps.put("bar", Long.valueOf(60 * 15));
				cacheManager.setExpires(exps);
			}
		};
	}
}
