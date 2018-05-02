package com.sky.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.io.File;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.MultipartProperties;
import org.springframework.boot.autoconfigure.web.WebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.sky.mybatis.mybatisSpringBootCommonMapper.domain.GradeEvent;
import com.sky.mybatis.mybatisSpringBootCommonMapper.service.CommonMapperService;
import com.sky.spring.mvc.web.HomeController;

@RunWith(SpringRunner.class)
// 这个注解只会扫描spring mvc相关的注解类
@WebMvcTest({HomeController.class})
@EnableConfigurationProperties(MultipartProperties.class)
public class SpringMvcTest {
	Logger logger = LoggerFactory.getLogger(SpringMvcTest.class);
	@Autowired
    private MockMvc mvc;
	@MockBean
	private CommonMapperService commonMapperService;
	@Autowired
	private Environment env;
	@Autowired
	MultipartProperties multipartProperties;
	RestTemplate restTemplate = new RestTemplate();
	
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
	
	/**
	 * 怎么测试servlet？
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testServlet() throws Exception {
//		mvc.perform(get("/CarServlet"));
		restTemplate.getForEntity("http://localhost:8080/CarServlet", Object.class);
	}
	
	@Test
	public void testUpload() throws Exception {
		String name = "file"; // 这个一定要写成file，否则会报400 bad request错误。这是因为spring自身的异常，被自动转化为相应的http状态码了
		String originalFilename = "测试文件上传.txt";
		String contentType = ",multipart/form-data";
		byte[] contentStream = "文件内容abcd".getBytes("utf-8");
		String copyToPath = "/Users/jianghui/Downloads/temp-del/cachecloud-web";
		mvc.perform(fileUpload("/fileUpload").file(new MockMultipartFile(name, originalFilename, contentType, contentStream))
				.param("copyToPath", copyToPath))
			.andExpect(status().isOk());
		
		assertThat(new File(copyToPath + File.separator + originalFilename).exists()).isTrue();
		// 上传临时目录里的文件，貌似临时文件会被自动删除的？
		assertThat(new File(multipartProperties.getLocation() + File.separator + originalFilename).exists()).isFalse();
	}
	
	@Test
	public void testHandlerException() throws Exception {
		// 特定的spring异常会自动映射到指定的http状态码
		mvc.perform(get("/handlerException").contentType(MediaType.APPLICATION_FORM_URLENCODED))
			.andExpect(status().is(415));
		mvc.perform(get("/handlerException").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		mvc.perform(post("/handlerException").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().is(HttpStatus.METHOD_NOT_ALLOWED.value()));
		
		// 自定义异常
		mvc.perform(get("/handlerException").contentType(MediaType.APPLICATION_JSON).param("throwNotAccepableException", "1"))
			.andExpect(status().isNotAcceptable());
		
		// 使用方法统一处理异常
		mvc.perform(get("/handlerException").contentType(MediaType.APPLICATION_JSON).param("throwMethodException", "1"))
			.andExpect(view().name("dealedMethodException"));
		
		// 测试ControllerAdvice
		mvc.perform(get("/handlerException").contentType(MediaType.APPLICATION_JSON).param("throwCommonException", "1"))
			.andExpect(view().name("dealedCommonException"));
		
	}
	
	@Test
	public void testRedirectPassParam() throws Exception {
		mvc.perform(get("/redirectBefore"))
			.andExpect(status().is3xxRedirection());
	}
	
}