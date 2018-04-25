package com.sky.movie;

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

import com.sky.movie.mybatis.domain.User;
import com.sky.movie.mybatis.service.BarService;
import com.sky.movie.mybatisMultiDataSource.config.ds.DS;
import com.sky.movie.mybatisMultiDataSource.config.ds.SampleClusterDataSourceConfig;
import com.sky.movie.mybatisMultiDataSource.config.ds.SampleDynamicDataSourceConfig;
import com.sky.movie.mybatisMultiDataSource.config.ds.SampleMasterDataSourceConfig;
import com.sky.movie.mybatisMultiDataSource.data.cluster.StudentMapper3;
import com.sky.movie.mybatisMultiDataSource.data.master.UserMapper3;
import com.sky.movie.mybatisSpring.config.SampleConfig;
import com.sky.movie.mybatisSpringBoot.config.SampleConfig2;
import com.sky.movie.mybatisSpringBoot.data.UserMapper2;

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
	@ComponentScan("com.sky.movie")
	@MapperScan(basePackages = "com.sky.movie.mybatisMultiDataSource.data")
//	@Import({SampleMasterDataSourceConfig.class, SampleClusterDataSourceConfig.class})
	@Import({SampleDynamicDataSourceConfig.class})
//	@MapperScan("com.sky.movie.mybatisSpringBoot.data")
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
