package com.sky.allinone.webSocket.controller;

import java.security.Principal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.sky.allinone.dao.entity.GradeEvent;

@Controller
public class StompController {
	private static final Logger logger = LoggerFactory.getLogger(StompController.class);
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	/*
	 * Stomp帧：类似http协议，包括三大部分
	 * 命令、头部信息、空行+消息体（负载）
	 */
	
	/**
	 * 会隐含在setApplicationDestinationPrefixes里配置的前缀，也就是处理/app/hiStomp的消息。
	 * stomp不同于http，消息转换器只有byteArray、String、json三种，所有方法入参要注意能不能被正确转码。
	 * 
	 * 
	 * @param incoming
	 * @return 如果方法有返回值的话，返回值将会被自动加上“/topic”前缀后，发送消息代理；也可以通过@SendTo注解改变消息目的地
	 */
	@MessageMapping("/hiStomp")
	@SendTo("/topic/hiStomp") // 改变消息目的地，所有订阅这个主题的应用（如客户端），都将会收到消息
	public String handlerReceive(String incoming) {
		logger.info("收到stomp消息：{}", incoming);
		broadcastStompMsg();
		return "我已收到消息，请放心";
	}
	
	/**
	 * 场景：客户端订阅了/app/backHiStomp
	 * 路径：直接返回客户端，不经过消息代理
	 * 
	 * 这个方法是在什么时候触发的：一旦有客户端订阅本消息，立马发送给客户端
	 * @return
	 */
	@SubscribeMapping("/backHiStomp")
	public String handlerSubscription() {
		String msg = "客户端订阅的@SubscribeMapping消息";
		logger.info("@SubscribeMapping：{}", msg);
		return msg;
	}
	
	/**
	 * 向某一主题的所有订阅者广播消息
	 * 这种可以实现RSS效果，实时推送
	 */
	public void broadcastStompMsg() {
		GradeEvent gradeEvent = new GradeEvent();
		gradeEvent.setCategory("使用SimpMessagingTemplate发送的广播消息");
		gradeEvent.setDate(new Date());
		gradeEvent.setEventId("1");
		simpMessagingTemplate.convertAndSend("/topic/broadcastStompMsg", gradeEvent);
	}
	
	/**
	 * TODO
	 * 会根据stomp帧上的头信息获取到Principal信息
	 * 
	 * 暂时没有实现安全访问、发送给特定用户、通用的异常处理
	 * @param Principal
	 * @param msg
	 * @return
	 */
	@MessageMapping("/dealWithUser")
	@SendToUser("/queue/notifyUser")
	public String dealWithUser(Principal principal, String msg) {
		logger.info("dealWithUser收到的消息：{}，登录用户信息，用户名：{}，principal：{}", msg, principal.getName(), principal.toString());
		return "得到已登录的用户消息，返回消息发送给特定用户";
	}
}
