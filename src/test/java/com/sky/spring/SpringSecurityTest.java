package com.sky.spring;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.sky.mybatis.mybatisSpringBootCommonMapper.service.CommonMapperService;
import com.sky.spring.mvc.web.HomeController;
import com.sky.spring.security.config.SecurityConfig;

@RunWith(SpringRunner.class)
@WebMvcTest({HomeController.class})
//@SpringBootTest
//@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class SpringSecurityTest {
	Logger logger = LoggerFactory.getLogger(SpringSecurityTest.class);
	@Autowired
    private MockMvc mvc;
	@MockBean
	private CommonMapperService commonMapperService;
	@MockBean
	private DataSource ds;
	
	/**
	 * 测试未认证
	 * @throws Exception
	 */
	@Test
	public void testSecurity() throws Exception {
		mvc.perform(get("/getGradeEvents"))
			.andExpect(status().isForbidden());
		mvc.perform(get("/getGradeEventsList"))
			.andExpect(status().isForbidden());
		mvc.perform(get("/findByid/1"))
			.andExpect(status().isForbidden());
		mvc.perform(get("/findByid/12"))
			.andExpect(status().isOk());
		mvc.perform(get("/handlerException"))
			.andExpect(status().isForbidden());
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
	 * 未测试到的点：
	 * 强制通道的安全性：也就是把http重定向到https
	 */
	@Test
	public void testTodo() {
		
	}
}
