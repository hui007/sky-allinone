package com.sky.allinone.remote.conf;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.BurlapServiceExporter;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;
import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import com.sky.allinone.remote.service.RemoteServiceInf;

@Configuration
public class RemoteConfiguration {
	/*
	 * 几种RPC方式的优缺点：
	 * 消息格式：
	 * 二进制：
	 * 	java序列化：RMI
	 * 	可移植二进制（PHP、Python、C++和C#可识别这种二进制）：Hessian
	 * XML格式：
	 * 	需额外外部定义语言：SOAP、XML-RPC
	 * 	不需额外外部定义语言：Burlap
	 * 
	 * 防火墙穿透性：
	 * 任意端口：RMI
	 * HTTP通道：Hessian、Burlap
	 * 
	 * 可移植性：
	 * 消息格式可移植
	 * 消息格式化方式可切换
	 * 联通协议：http、socket
	 */
	
	/**
	 * 会生成服务端的RemoteServiceInf代理实例
	 * @param servcieImpl
	 * @return
	 */
	@Bean(name = "/remoteService")
	public HessianServiceExporter hessianServiceExporter(@Qualifier("remoteServiceImpl") RemoteServiceInf servcieImpl) {
		HessianServiceExporter exporter = new HessianServiceExporter();
		exporter.setService(servcieImpl);
		exporter.setServiceInterface(RemoteServiceInf.class);
		
		return exporter;
	}
	
	/**
	 * 已经不建议使用burlap方式了
	 * @param servcieImpl
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public BurlapServiceExporter burlapServiceExporter(@Qualifier("remoteServiceImpl") RemoteServiceInf servcieImpl) {
		BurlapServiceExporter exporter = new BurlapServiceExporter();
		exporter.setService(servcieImpl);
		exporter.setServiceInterface(RemoteServiceInf.class);
		
		return exporter;
	}
	
	/**
	 * 使用这种方式没调试通过，所以改用上面的方法@Bean(name = "/remoteService")，客户端请求后，会使用beanName解析器解析到服务类
	 * HessianServiceExporter本质上相当于一个控制器，通过dispatchServlet接收请求，然后转发到服务实现类
	 * @return
	 */
//	@Bean
	public HandlerMapping hessianMapping() {
		SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
		Properties properties = new Properties();
		properties.setProperty("/hessian.service", "hessianServiceExporter");
		mapping.setMappings(properties);
		
		return mapping;
	}
	
	/**
	 * 模拟客户端
	 * 工厂bean，会生成客户端的RemoteServiceInf代理实例
	 * @return
	 */
	@Bean
	public HessianProxyFactoryBean hessianServiceClient() {
		HessianProxyFactoryBean proxy = new HessianProxyFactoryBean();
		proxy.setServiceUrl("http://localhost:8081/remoteService");
		proxy.setServiceInterface(RemoteServiceInf.class);
		
		return proxy;
	}
}
