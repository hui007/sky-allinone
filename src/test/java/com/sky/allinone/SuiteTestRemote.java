package com.sky.allinone;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.sky.allinone.actuator.ActuatorTest;
import com.sky.allinone.cache.CacheTest;
import com.sky.allinone.email.EmailTest;
import com.sky.allinone.jmx.JmxTest;
import com.sky.allinone.mybatis.MybatisSuiteTest;
import com.sky.allinone.redis.RedisTest;
import com.sky.allinone.remote.RemoteTest;
import com.sky.allinone.spring.SpringSecurityTest;
import com.sky.allinone.spring.SpringSuiteTest;
import com.sky.allinone.webSocket.WebSocketTest;

/**
 * 这些测试用例需要先启动springboot应用，作为客户端调用远程应用
 * 必须激活remote profile  -Dspring.profiles.active=remote
 * @author joshui
 *
 */
@RunWith(Suite.class)  
@Suite.SuiteClasses({RemoteTest.class, JmxTest.class, ActuatorTest.class})  
public class SuiteTestRemote {

}
