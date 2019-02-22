package com.sky.allinone.webSocket.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * 
 * @author joshui
 *
 */
@Configuration  
@EnableWebSocketMessageBroker
public class WebSocketStompConfiguration extends AbstractWebSocketMessageBrokerConfigurer{

	/* 
	 * 可以在握手时就获得user，判断是否登录。
	 * 使用addInterceptors、setHandshakeHandler等方法。
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/stomp").withSockJS();
	}
	
	/* 
	 * 简单代理和代理中继的区别：
	 * 可用性：简单代理是内存模拟的；代理中继使用的是真正的消息服务器，如rabbitMQ、activeMQ
	 * 协议：简单代理只支持stomp的部分协议；代理中继支持全部的stomp协议
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// 代理的目的地：简单代理
		registry.enableSimpleBroker("/queue", "/topic");
		// 代理的目的地：另外的消息服务器。这里是启用了代理中继，将消息转发到消息服务器
//		registry.enableStompBrokerRelay("/queue", "/topic")
//			.setRelayHost("rabbit.someoneotherserver")
//			.setRelayPort(62623)
//			.setClientLogin("用户名")
//			.setClientPasscode("密码");
		
		// 应用程序的目的地：最终也需要回到代理，返回给客户端
		registry.setApplicationDestinationPrefixes("/app");
		
	}
	
}
