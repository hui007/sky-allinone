package com.sky.allinone.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.access.MBeanProxyFactoryBean;
import org.springframework.jmx.support.MBeanServerConnectionFactoryBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.sky.allinone.SkyAllinoneApplication;
import com.sky.allinone.jmx.conf.JmxConfig;
import com.sky.allinone.jmx.service.JmxGrandEventService;
import com.sky.allinone.jmx.service.JmxGrandEventServiceInf;

@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) 因为定义了内部@Configuration，所以再定义webEnvironment
@SpringBootTest()
public class JmxTest {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private MBeanServerConnection connection;
	@Autowired
	JmxGrandEventServiceInf jmxGrandEventServiceInf;
	
	/**
	 * 必须先启动项目进程，这个测试用例作为jmx客户端调用项目进程jmx服务端。
	 * 启动项目进程时，必须激活profile remote（-Dspring.profiles.active=remote）
	 * @author joshui
	 *
	 */
	@Configuration
	static class AppConfig{
		/**
		 * 客户端使用的连接器
		 * @return
		 * @throws MalformedURLException
		 */
		@Bean
		public MBeanServerConnectionFactoryBean mBeanServerConnectionFactoryBean() throws MalformedURLException {
			MBeanServerConnectionFactoryBean factory = new MBeanServerConnectionFactoryBean();
			factory.setServiceUrl(
	                String.format("service:jmx:rmi://%s/jndi/rmi://%s:%s/jmxrmiTest", JmxConfig.rmiHost, JmxConfig.rmiHost, JmxConfig.rmiPort));
			
			return factory;
		}
		
		@Bean
		public MBeanProxyFactoryBean mBeanProxyFactoryBean(MBeanServerConnection con) throws MalformedURLException, MalformedObjectNameException {
			MBeanProxyFactoryBean proxy = new MBeanProxyFactoryBean();
			proxy.setObjectName("com.sky.allinone:name=JmxGrandEventService");
			proxy.setServer(con);
			proxy.setProxyInterface(JmxGrandEventServiceInf.class);
			
			return proxy;
		}
	}

	@Test
	public void testJmxConnect() throws IOException {
		Integer count = connection.getMBeanCount();
		logger.info("count:{}", count);
		Set<ObjectName> queryNames = connection.queryNames(null, null);
		logger.info("queryNames:{}", queryNames);
		
	}
	
	@Test
	public void testJmxInvoke() throws IOException, AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, InvalidAttributeValueException {
		ObjectName remoteObj = new ObjectName("com.sky.allinone:name=JmxGrandEventService");
		
		// 访问属性。属性名字以大写开头，默认的？
		String attrName = "Attr";
		connection.setAttribute(remoteObj, new Attribute(attrName, "before"));
		Object attribute = connection.getAttribute(remoteObj, attrName);
		assertThat(attribute).isEqualTo("before");
		connection.setAttribute(remoteObj, new Attribute(attrName, "after"));
		attribute = connection.getAttribute(remoteObj, attrName);
		assertThat(attribute).isEqualTo("after");
		
		// 访问方法
		Object invoke = connection.invoke(remoteObj, "sayHi", null, null);
		assertThat(invoke).isEqualTo("你好，我的朋友");
	}
	
	@Test
	public void testProxy() throws IOException {
		String hi = jmxGrandEventServiceInf.sayHi();
		assertThat(hi).isEqualTo("你好，我的朋友");
	}
	
	/**
	 * 待测试：
	 * jmx通知机制
	 * @throws IOException
	 */
	@Test
	public void testTodo() throws IOException {
		
	}
}
