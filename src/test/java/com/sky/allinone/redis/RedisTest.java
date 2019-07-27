package com.sky.allinone.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.endsWith;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import com.sky.allinone.dao.entity.GradeEvent;
import com.sky.allinone.service.RedisService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
	private Logger logger = LoggerFactory.getLogger(getClass());
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
	 * 测试lua脚本
	 */
	@Test
	public void testLua() {
//		DefaultRedisScript<Set<TypedTuple<String>>> redisScript = new DefaultRedisScript<Set<TypedTuple<String>>>();
		DefaultRedisScript<List> redisScript = new DefaultRedisScript<List>();
		org.springframework.core.io.Resource scriptLocation = new ClassPathResource("lua/zset.lua");
		redisScript.setLocation(scriptLocation);
		redisScript.setResultType(List.class);
		
		List<String> list = new ArrayList<>();
		list.add("myzset:processingset");
		list.add("myzset:initset");
		final Integer limit = 100;
//		Set<TypedTuple<String>> elements = stringRedisTemplate.execute(redisScript, list, 0,limit,"WITHSCORES");
//		redisTemplate.execute(redisScript, list, new Object[]{ new Integer(0), limit});
		
		final List<String> execute = stringRedisTemplate.<List>execute(redisScript, list, String.valueOf(0), String.valueOf(limit));
		System.out.println(execute);
		
//		redisTemplate.execute(new RedisScript<Set>() {
//			
//			@Override
//			public String getSha1() {
//				// TODO Auto-generated method stub
//				return null;
//			}
//			
//			@Override
//			public Class getResultType() {
//				return Set.class;
//			}
//			
//			@Override
//			public String getScriptAsString() {
//				try {
//					return FileUtils.readFileToString(scriptLocation.getFile());
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				return "";
//			}
//		}, list, new Object[]{ limit, "WITHSCORES"});
		
//		logger.info(elements.toString());
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
	 * 还有很多功能没有测试，具体可以参看官方文档，或者参考《redis开发与运维》
	 * 比如：sentinel模式、集群模式、各种数据类型的测试、pipeline、lua、事务等
	 */
	@Test
	public void testTodo() {
		/*
		 * 数据结构：
		 * BitMap：本质上也是string，只不过可以对位进行操作。如果初始容量过大，可能会造成redis阻塞。场景：独立访问用户总量，将每个用户的id映射到一个位上。
		 * HyperLogLog：极小的空间完成独立总数的计算。
		 */
		
		/*
		 * 缓存和数据库一致性：
		 * 1、先删除缓存，再更新数据库。问题：更新数据库之前，读取缓存会引入脏数据。
		 * 2、先更新数据库，成功后，删除缓存。问题：删除缓存，事务还没提交前，读取缓存会引入脏数据。
		 * 3、只更新缓存，不更新数据库，异步批量同步到数据库。
		 * 4、强一致性。使用二阶段提交或者分布式锁。前者是分布式事务，后者是防止并发读取或者修改。
		 */
		
		/*
		 * 缓存击穿：
		 * 缓存穿透：访问不存在的值，访问压力直接打向数据库。解决：缓存空值并设置过期时间；或使用布隆过滤器。
		 * 缓存击穿：也就是”热点key重建优化“。热点key失效后，大量线程访问数据库重建这个key。解决：设置互斥锁（不要并发重建）；或不设置过期时间。
		 * 雪崩优化：redis挂了后，流量全打向数据库。解决：缓存服务高可用，限流、降级。
		 */
	}
}
