package com.sky.mybatis;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.test.context.junit4.SpringRunner;

import com.sky.mybatis.mybatis.domain.User;
import com.sky.mybatis.mybatis.service.BarService;
import com.sky.mybatis.mybatisMultiDataSource.config.ds.DS;
import com.sky.mybatis.mybatisMultiDataSource.config.ds.SampleClusterDataSourceConfig;
import com.sky.mybatis.mybatisMultiDataSource.config.ds.SampleDynamicDataSourceConfig;
import com.sky.mybatis.mybatisMultiDataSource.config.ds.SampleMasterDataSourceConfig;
import com.sky.mybatis.mybatisMultiDataSource.data.cluster.StudentMapper3;
import com.sky.mybatis.mybatisMultiDataSource.data.master.UserMapper3;
import com.sky.mybatis.mybatisSpring.config.SampleConfig;
import com.sky.mybatis.mybatisSpringBoot.config.SampleConfig2;
import com.sky.mybatis.mybatisSpringBoot.data.UserMapper2;

/**
 * 集成spring boot，测试多数据源
 * @author joshui
 *
 */
@RunWith(SpringRunner.class)
//@SpringBootTest(classes = {SampleConfig2.class})
@SpringBootTest()
public class MybatisSpringBootMultiDSTest {
	@Autowired
	private UserMapper3 UserMapper;
	@Autowired
	private StudentMapper3 studentMapper;
	@Autowired
	BarService barService;
	
	@Configuration
	@ComponentScan("com.sky.mybatis")
	@MapperScan(basePackages = "com.sky.mybatis.mybatisMultiDataSource.data")
//	@Import({SampleMasterDataSourceConfig.class, SampleClusterDataSourceConfig.class})
	@Import({SampleDynamicDataSourceConfig.class})
//	@MapperScan("com.sky.mybatis.mybatisSpringBoot.data")
	static class AppConfig {
	}
	
	@Test
	public void testQuery1() {
		User user = UserMapper.getUser(1);
		assertThat(user.getName()).isEqualTo("admin");
	}
	
	@Test(expected = BadSqlGrammarException.class)
//	@DS("clusterDataSource")
	public void testQuery2() {
		Map<String, String> student = barService.doSomeBusinessStuff(1);
		assertThat(student.get("name")).isEqualTo("Megan");
		Map<String, String> student1 = studentMapper.getStudent(1);
	}
}
