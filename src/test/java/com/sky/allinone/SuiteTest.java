package com.sky.allinone;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.sky.allinone.cache.CacheTest;
import com.sky.allinone.email.EmailTest;
import com.sky.allinone.mybatis.MybatisSuiteTest;
import com.sky.allinone.redis.RedisTest;
import com.sky.allinone.spring.SpringSecurityTest;
import com.sky.allinone.spring.SpringSuiteTest;
import com.sky.allinone.webSocket.WebSocketTest;

@RunWith(Suite.class)  
@Suite.SuiteClasses({MybatisSuiteTest.class,SpringSuiteTest.class, RedisTest.class, CacheTest.class,
	SpringSecurityTest.class, WebSocketTest.class,EmailTest.class})  
public class SuiteTest {

}
