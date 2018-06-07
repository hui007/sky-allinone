package com.sky.allinone.email;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sky.allinone.email.service.EmailService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailTest {
	@Autowired
	EmailService emailService;
	
	/*
	 * 接种邮件协议的说明：
	 * SMTP 的全称是“Simple Mail Transfer Protocol”，即简单邮件传输协议，用于邮件发送。SMTP
	 * 认证，简单地说就是要求必须在提供了账户名和密码之后才可以登录 SMTP 服务器。
	 * 
	 * POP3（Post Office Protocol 3）协议允许电子邮件客户端下载服务器上的邮件，但是在客户端的
	 * 操作（如移动邮件、标记已读等），不会反馈到服务器上，比如通过客户端收取了邮箱中的3封邮件并
	 * 移动到其他文件夹，邮箱服务器上的这些邮件是没有同时被移动的 。
	 * 
	 * 而IMAP（Internet Mail Access Protocol）提供webmail 与电子邮件客户端之间的双向通信，客
	 * 户端的操作都会反馈到服务器上，对邮件进行的操作，服务器上的邮件也会做相应的动作。
	 * 同时，IMAP像POP3那样提供了方便的邮件下载服务，让用户能进行离线阅读。IMAP提供的摘要浏览
	 * 功能可以让你在阅读完所有的邮件到达时间、主题、发件人、大小等信息后才作出是否下载的决定。此
	 * 外，IMAP 更好地支持了从多个不同设备中随时访问新邮件。
	 * 总之，IMAP 整体上为用户带来更为便捷和可靠的体验。POP3 更易丢失邮件或多次下载相同的邮
	 * 件，但 IMAP 通过邮件客户端与webmail 之间的双向同步功能很好地避免了这些问题。
	 */
	
	@Test
	public void testSendText() {
		emailService.sendSimple();
	}
	
	/**
	 * 待测试内容：
	 * 当前使用的是配置文件配置的邮件发送服务器信息，也可以使用jndi获取邮件服务器信息；
	 * 添加附件；
	 * 使用富文本邮件内容；
	 * 使用模板构建邮件内容；
	 * 接收邮件；
	 */
	@Test
	public void testTodo() {
		
	}
}
