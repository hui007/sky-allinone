package com.sky.movie;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.sky.movie.mybatis.domain.User;
import com.sky.movie.mybatisSpring.config.SampleConfig;
import com.sky.movie.mybatisSpring.service.FooService;

/**
 * 集成spring，使用java config配置
 * 
 * @author joshui
 *
 */
@RunWith(SpringRunner.class)
//@ContextConfiguration( locations = {"classpath:/application-dataSource.properties"})
//@ContextConfiguration(classes = {SampleConfig.class})
//@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
//@SpringBootTest(classes = {SampleConfig.class})
@SpringBootTest(classes = {SampleConfig.class})
public class MybatisSpringJavaConfigTest {
	@Autowired
	private FooService fooService;

	@Autowired
	private FooService fooServiceWithMapperFactoryBean;

	@Test
	public void test() {
		User user = fooService.doSomeBusinessStuff(1);
		assertThat(user.getName()).isEqualTo("admin");
	}

	@Test
	public void testWithMapperFactoryBean() {
		User user = fooServiceWithMapperFactoryBean.doSomeBusinessStuff(1);
		assertThat(user.getName()).isEqualTo("admin");
	}
}
