package com.sky.movie;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.movie.mybatis.domain.User;
import com.sky.movie.mybatisSpring.config.SampleConfig;
import com.sky.movie.mybatisSpringBoot.config.SampleConfig2;
import com.sky.movie.mybatisSpringBoot.data.UserMapper2;

/**
 * 集成spring boot
 * @author joshui
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SampleConfig2.class})
//@SpringBootTest()
public class MybatisSpringBootTest {
	@Autowired
	private UserMapper2 UserMapper2;
	@Autowired
	private com.sky.movie.mybatis.data.UserMapper1 UserMapper1;
	
	// 自定义一个@SpringBootApplication，屏蔽掉外部的@SpringBootApplication，但是其他test类跑的时候，还是会发现这里定义的@SpringBootApplication
//	@Configuration
//	@SpringBootApplication(exclude = {  
//	        DataSourceAutoConfiguration.class  
//	})
//	@Import(SampleConfig2.class)
//	@MapperScan("com.sky.movie.mybatisSpringBoot.data")
	static class AppConfig {
	}
	
	/**
	 * 测试查询
	 */
	@Test
	public void testQuery() {
		User user = UserMapper2.getUser(1);
		assertThat(user.getName()).isEqualTo("admin");
	}
	
	/**
	 * 测试分页查询
	 */
	@Test
	@SuppressWarnings("unused")
	public void testQueryPage() {
		Page<User> page = PageHelper.startPage(1, 1).doSelectPage(()-> UserMapper1.getUserByPage());
		PageInfo<Object> pageInfo = PageHelper.startPage(1, 1).doSelectPageInfo(()-> UserMapper1.getUserByPage());
		long count = PageHelper.count(()-> UserMapper1.getUserByPage());
		
		// 除了这个方式之外，还有其他几种方式，具体参见官方文档
		PageHelper.startPage(1, 1);
//		PageHelper.orderBy("");
		List<User> users = UserMapper1.getUserByPage();
		assertThat(users.size()).isEqualTo(1);
		assertThat(users.get(0).getName()).isEqualTo("admin");
	}
}
