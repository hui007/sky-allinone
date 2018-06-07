package com.sky.allinone.email.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	@Autowired
    private JavaMailSender javaMailSender;
	@Value("${spring.mail.username}")
    private String username;
	
	public void sendSimple() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(username);
        message.setTo("48536047@qq.com");
        message.setSubject("标题：测试标题");
        message.setText("测试内容部份");
		javaMailSender.send(message);
    }
}
