package com.sky.allinone.spring;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.sky.allinone.service.CommonMapperService;

@RunWith(SpringRunner.class)
/*
@WebMvcTest({HomeController.class})
@Import(SecurityConfig.class)
*/
@SpringBootTest
@AutoConfigureMockMvc
public class SpringSecurityTest {
	Logger logger = LoggerFactory.getLogger(SpringSecurityTest.class);
	@Autowired
    private MockMvc mvc;
	@MockBean
	private CommonMapperService commonMapperService;
	// @MockBean(name = "dynamicDataSource") No qualifying bean of type 'javax.sql.DataSource' available: expected single matching bean but found 3: clusterDataSource,masterDataSource,dynamicDataSource
	@Autowired
	private DataSource ds;
	
	/**
	 * 测试未认证
	 * @throws Exception
	 */
	@Test
	public void testSecurity() throws Exception {
		// 对于未认证的。如果配置了访问未认证资源，跳转到登录页面，则返回302重定向；如果没有配置，则返回403，forbidden
		mvc.perform(get("/getGradeEvents"))
			.andExpect(status().is3xxRedirection());
		mvc.perform(get("/getGradeEventsList"))
			.andExpect(status().is3xxRedirection());
		mvc.perform(get("/findByid/1"))
			.andExpect(status().is3xxRedirection());
		mvc.perform(get("/findByid/12"))
			.andExpect(status().isOk());
		mvc.perform(get("/handlerException"))
			.andExpect(status().is3xxRedirection());
		mvc.perform(get("/homepage"))
			.andExpect(status().isOk());
	}
	
	/**
	 * 测试已认证
	 * @throws Exception
	 */
	@Test
	public void testAuth() throws Exception {
		mvc.perform(get("/getGradeEvents").with(user("user")))
			.andExpect(status().isOk());
		mvc.perform(get("/handlerException")
				.with(user("amdin").password("adminpw").roles("ADMIN", "USER"))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(roles="USER")
	public void testSpel() throws Exception {
		mvc.perform(get("/redirectBefore")
				/*.with(user("user").password("userpw").roles("USER"))*/)
			.andExpect(status().is3xxRedirection());
	}
	
	/**
	 * 测试自定义登录页面
	 * @throws Exception 
	 */
	@Test
	public void testCustomerLoginPage() throws Exception {
		// 未登录，跳转到自定义的登录页面
		mvc.perform(get("/getGradeEvents"))
			.andExpect(redirectedUrl("http://localhost/showLoginPage"));
		
		// 登录成功
		mvc.perform(post("/login").param("username", "user").param("password", "userpw"))
			.andExpect(redirectedUrl("/welcomePage"));
		
		// 登录失败
		mvc.perform(post("/login").param("username", "whoami").param("password", "userpw"))
			.andExpect(forwardedUrl("/showLoginPagePost?myError=自定义登录失败url"));
	}
	
	/**
	 * 未测试到的点：Security框架提供了支持，只是本测试用例还没测试
	 * 1、强制通道的安全性：也就是把http重定向到https
	 * 2、CSRF跨站请求伪造
	 * 典型攻击场景：用户U在A网站登录后，先开一个tab页，打开B网站，B网站有一个图片，点击后请求A网站某个转账URL，此时请求会发到A的后台，同时请求会带上当前Cookie
	 * 针对同一浏览器（XSS是针对同一用户？）；针对能在服务器内部改变用户状态的请求（浏览器同源策略，黑客解析不了非同源的返回数据）
	 * 应对措施：检查refer（浏览器可能禁用refer、低版本浏览器可能被攻破refer）、token（url token、表单token、动态token）、在http头自定义属性。这三种措施各有优缺点
	 */
	@Test
	public void testTodo() {
		
	}
}
