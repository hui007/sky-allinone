package com.sky.allinone;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.sky.allinone.cache.CacheTest;
import com.sky.allinone.cicd.CICDTest;
import com.sky.allinone.elasticsearch.ElasticsearchTest;
import com.sky.allinone.email.EmailTest;
import com.sky.allinone.java.JavaSuiteTest;
import com.sky.allinone.java.JavaTest;
import com.sky.allinone.java.file.PDFTest;
import com.sky.allinone.jmx.JmxTest;
import com.sky.allinone.kafka.KafkaTest;
import com.sky.allinone.mybatis.MybatisSuiteTest;
import com.sky.allinone.netty.NettyTest;
import com.sky.allinone.redis.RedisTest;
import com.sky.allinone.spring.SpringSecurityTest;
import com.sky.allinone.spring.SpringSuiteTest;
import com.sky.allinone.webSocket.WebSocketTest;
import com.sky.allinone.zookeeper.ZookeeperTest;

@RunWith(Suite.class)  
@Suite.SuiteClasses({
	// 需要启动本机mysql服务器、本机redis服务器、本机的zookeeper、本机kafka服务器
	MybatisSuiteTest.class,RedisTest.class, CacheTest.class,KafkaTest.class,
	
	SpringSuiteTest.class, 
	SpringSecurityTest.class, WebSocketTest.class,EmailTest.class,ElasticsearchTest.class,JavaSuiteTest.class,
	NettyTest.class,ZookeeperTest.class,CICDTest.class})  
public class SuiteTest {

}
