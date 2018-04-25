package com.sky.movie;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.movie.mybatis.domain.User;
import com.sky.movie.mybatisMultiDataSource.config.ds.DynamicDataSourceAspect;
import com.sky.movie.mybatisMultiDataSource.config.ds.SampleDynamicDataSourceConfig;
import com.sky.movie.mybatisSpringBoot.data.UserMapper2;
import com.sky.movie.mybatisSpringBootCommonMapper.data.GradeEventMapper;
import com.sky.movie.mybatisSpringBootCommonMapper.domain.GradeEvent;
import com.sky.movie.mybatisSpringBootCommonMapper.service.CommonMapperService;

/**
 * 集成spring boot，测试common mapper
 * @author joshui
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
public class MybatisSpringBootCommonMapperTest {
	@Autowired
	CommonMapperService commonMapperService;
	@Autowired
	DynamicDataSourceAspect dynamicDataSourceAspect;
	
	@Configuration
	// 难怪切面不生效
	@EnableAspectJAutoProxy
	@Import({SampleDynamicDataSourceConfig.class})
	@tk.mybatis.spring.annotation.MapperScan(basePackages = "com.sky.movie.mybatisSpringBootCommonMapper")
	static class AppConfig {
	}
	
	/**
	 * 测试查询
	 */
	@Test
	public void testQuery() {
		List<GradeEvent> selectAll = commonMapperService.doSomeBusinessStuff();
		assertThat(selectAll.size()).isEqualTo(6);
	}
}
