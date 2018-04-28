package com.sky.spring;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.Arrays;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.mockito.BDDMockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.sky.mybatis.mybatisSpringBootCommonMapper.domain.GradeEvent;
import com.sky.mybatis.mybatisSpringBootCommonMapper.service.CommonMapperService;
import com.sky.spring.mvc.web.HomeController;

@RunWith(SpringRunner.class)
// 这个注解只会扫描spring mvc相关的注解类
@WebMvcTest({HomeController.class})
public class SpringMvcTest {
	Logger logger = LoggerFactory.getLogger(SpringMvcTest.class);
	@Autowired
    private MockMvc mvc;
	@MockBean
	private CommonMapperService commonMapperService;
	@Autowired
	private Environment env;
	
	/**
	 * 这个测试方法，可以完全不使用SpringMvcTest类上的注解
	 * @throws Exception
	 */
	@Test
	public void testMvc() throws Exception {
		HomeController homeController = new HomeController();
		MockMvc mockMvc = standaloneSetup(homeController).build();
		
		mockMvc.perform(get("/")).andExpect(view().name("home"));
		mockMvc.perform(get("/homepage")).andExpect(view().name("home"));
	}
	
	@Test
	public void testService() throws Exception {
		given(this.commonMapperService.doSomeBusinessStuff())
        	.willReturn(Arrays.asList(new GradeEvent(), new GradeEvent()));
		
		mvc.perform(get("/getGradeEvents"))
			.andExpect(view().name("getGradeEvents"))
			.andExpect(model().attributeExists("gradeEvents"))
			.andExpect(model().attribute("gradeEvents", hasItem(commonMapperService.doSomeBusinessStuff().get(0))))
			.andExpect(model().attribute("gradeEvents", hasItems(commonMapperService.doSomeBusinessStuff().toArray())));
		
		mvc.perform(get("/getGradeEventsList"))
			.andExpect(view().name("getGradeEventsList"))
			.andExpect(model().attributeExists("gradeEventList"))
			.andExpect(model().attribute("gradeEventList", hasItem(commonMapperService.doSomeBusinessStuff().get(0))))
			.andExpect(model().attribute("gradeEventList", hasItems(commonMapperService.doSomeBusinessStuff().toArray())));
		
		mvc.perform(get("/findGradeEventsList?count=10"))
			.andExpect(view().name("findGradeEvent"))
			.andExpect(model().attributeExists("name", "count"))
			.andExpect(model().attribute("name", equalTo("sky")))
			.andExpect(model().attribute("count", equalTo(10)));
		
		mvc.perform(get("/findByid/2"))
			.andExpect(request().attribute("id", equalTo(2)));
		
		mvc.perform(post("/saveGradeEvent")
					.param("eventId", "1")
					.param("date", "20180428")
				)
//			.andExpect(model().attributeExists("gradeEvent"))
			.andExpect(status().is3xxRedirection())
			.andExpect(status().is(302))
			.andExpect(redirectedUrl("/showGradeEventInfo/1"));
	}
	
	@Test
	public void testValidate() throws Exception {
		logger.warn("javax.validation.constraints.Size.message", env.getProperty("javax.validation.constraints.Size.message"));
		mvc.perform(post("/testValidate").param("eventId", "1").param("date", "20180428"))
			.andExpect(forwardedUrl("/saveGradeEvent.html"));
			
	}
}
