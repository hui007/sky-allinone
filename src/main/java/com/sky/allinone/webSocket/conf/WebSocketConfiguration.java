package com.sky.allinone.webSocket.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.sky.allinone.webSocket.handler.WebSocketHandler;

/**
 * 通过一个socket，实现全双工通信。
 * websocket可应用于所有应用之间，但一般只用于应用和浏览器之间。
 * 
 * 兼容性：目前浏览器、应用服务器以及防火墙对websocket的支持力度不同，有可能不支持websocket。比如防火墙只支持http协议，或者还没来得及开启websocket支持。
 * 后备方案：SockJS。它会一次选择可用方案：XHR流、XDR流、iFrame事件源、iFrame html文件、XHR轮询、XDR轮询、iFrame XHRl轮询、jsonp轮询。
 * 
 * @author joshui
 *
 */
@Configuration  
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer{

	/* 
	 * 这个url必须要能被dispatchServlet拦截到。
	 * 
	 * 设置跨域接受的跨越地址：setAllowedOrigins;
	 * 启用sockJs：withSockJS()。
	 */
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(chickenHandler(), "/marco").setAllowedOrigins("http://mydomain.com").withSockJS();
	}
	
	@Bean
	public WebSocketHandler chickenHandler() {
		return new WebSocketHandler();
	}
	
}
