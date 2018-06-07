package com.sky.allinone.webSocket.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

/**
 * websocket的服务端处理器，处理某一websocket url发送的消息
 * @author joshui
 *
 */
public class WebSocketHandler extends AbstractWebSocketHandler{
	private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		logger.info("接收到消息：{}", message.getPayload());
		Thread.sleep(2000);
		session.sendMessage(new TextMessage("好，老地方，大吉大利！"));
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		logger.info("连接建立：{}", session.getId());
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		logger.info("连接断开：{}，{}", session.getId(), status.getCode());
	}
}
