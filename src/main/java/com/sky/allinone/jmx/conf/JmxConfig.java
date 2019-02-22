package com.sky.allinone.jmx.conf;

import java.net.MalformedURLException;
import java.util.HashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.jmx.support.MBeanServerConnectionFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;

import com.sky.allinone.jmx.service.JmxGrandEventService;

@Configuration
@Profile("remote")
public class JmxConfig {
	public static String rmiHost = "localhost";
	public static int rmiPort = 1099;
	
	/*
	 * springboot的自动配置：JmxAutoConfiguration
	 * 
	 * 导出bean：通过MBeanExporter
	 * mbean服务器：spring内置的mbena服务器，或者应用服务器提供的（比如tomcat）
	 * 调用链：以jconsole为例，jconsole、mbean服务器、mbean导出器、实际bean。
	 * 
	 * 暴露bean可被管理的方法：统一通过信息装配器
	 * 通过列举方法名称、通过接口定义、使用注解
	 */
	
	/**
	 * 
	 * @param jmxService
	 * @return
	 */
//	@Bean
//	public MBeanExporter mBeanExporter(JmxService jmxService) {
//		MBeanExporter exporter = new MBeanExporter();
//		HashMap<String, Object> beans = new HashMap<String, Object>();
//		beans.put("jmxService:name=JmxService", jmxService);
//		
//		return exporter;
//	}

	/**
	 * 默认使用jmxmp协议，但是使用这个协议时，还需要下载jmxmp的实现包。
	 * 那jconsole是通过什么协议连接jmxServer的，如果是rmi的话，会不会可能被防火墙拦截？
	 * @return
	 */
	@Bean
	@DependsOn("rmiRegistryFactoryBean")
	public ConnectorServerFactoryBean connectorServerFactoryBean() {
		ConnectorServerFactoryBean connectorServerFactoryBean = new ConnectorServerFactoryBean();
		connectorServerFactoryBean.setServiceUrl(
                String.format("service:jmx:rmi://%s/jndi/rmi://%s:%s/jmxrmiTest", rmiHost, rmiHost, rmiPort));
		return connectorServerFactoryBean;
	}
	
	@Bean
	public RmiRegistryFactoryBean rmiRegistryFactoryBean() {
		RmiRegistryFactoryBean factory = new RmiRegistryFactoryBean();
		factory.setPort(rmiPort);
		
		return factory;
	}
	
}
