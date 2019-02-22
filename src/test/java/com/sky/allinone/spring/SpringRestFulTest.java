package com.sky.allinone.spring;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.sky.allinone.dao.entity.GradeEvent;

/**
 * 跟remote测试一样，必须先启动spring boot application，再运行本测试用例
 * @author joshui
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SpringRestFulTest {
	Logger logger = LoggerFactory.getLogger(SpringRestFulTest.class);
	RestTemplate restTemplate = new RestTemplate();
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@BeforeClass
	public static void init() {
		// 去掉spring Security
		System.setProperty("security.basic.authorize-mode", "none");
	}
	
	/**
	 * 测试rest风格接口
	 * 
	 * restTemplate：
	 * 方法分类原则：
	 * 	按照动作类型，get、put、delete、post等；
	 * 	按照方法重载：url的格式不同，不可变url、可变ulr，以map指明参数、可变url，以可变参数指定参数；
	 * 	按照能否指定额外信息：能否操作header信息、能否指定请求的header信息、能否获取响应header信息；
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testRestFul() {
		String url = "http://localhost:8081/rest/{methodName}";
		
		List<GradeEvent> forObject = restTemplate.getForObject(url, List.class, "getGradeEvents");
		assertThat(forObject.size()).isEqualTo(2);
		
		expectedEx.expect(HttpClientErrorException.class);
		GradeEvent forObject404 = restTemplate.getForObject(url, GradeEvent.class, "gradeEventByIdStatus");
		
		ResponseEntity<GradeEvent> forEntity = restTemplate.getForEntity(url, GradeEvent.class, "saveGradeEventWithHeaer");
		assertThat(forEntity.getBody().getCategory()).isEqualTo("changed");
		
		ResponseEntity<GradeEvent> forEntity404 = restTemplate.getForEntity(url, GradeEvent.class, "gradeEventByIdEntity");
		assertThat(forEntity404.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}  
	
}
