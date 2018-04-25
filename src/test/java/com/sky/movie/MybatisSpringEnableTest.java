package com.sky.movie;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.sky.movie.mybatis.domain.User;
import com.sky.movie.mybatisSpring.config.SampleConfig;
import com.sky.movie.mybatisSpring.service.FooService;

/**
 * 集成spring，使用mybatis mapper scan配置
 * 
 * @author joshui
 *
 */
@RunWith(SpringRunner.class)
//@ContextConfiguration( locations = {"classpath:/application-dataSource.properties"})
//@ContextConfiguration(classes = {SampleConfig.class})
//@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
//@SpringBootTest(classes = {SampleConfig.class})
@SpringBootTest()
public class MybatisSpringEnableTest {
	@Configuration
	@Import(SampleConfig.class)
//	@ImportResource("classpath:org/mybatis/spring/sample/config/applicationContext-infrastructure.xml")
	@MapperScan("com.sky.movie.mybatis.data")
	static class AppConfig {
	}
	
	@Autowired
	private FooService fooServiceMapperScan;

	@Test
	public void test() {
		User user = fooServiceMapperScan.doSomeBusinessStuff(1);
		assertThat(user.getName()).isEqualTo("admin");
	}
}
