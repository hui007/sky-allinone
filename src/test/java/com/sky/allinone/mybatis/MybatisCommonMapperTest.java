package com.sky.allinone.mybatis;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.allinone.dao.entity.GradeEvent;
import com.sky.allinone.dao.entity.User;
import com.sky.allinone.dao.mapper.GradeEventMapper;
import com.sky.allinone.dao.mapper.UserMapper;
import com.sky.allinone.service.CommonMapperService;

/**
 * 集成spring boot，测试common mapper，动态数据源
 * 没有使用内嵌数据库，需要启动本机mysql数据库，本机mysql的测试数据库脚本后续可考虑放到git上
 * TODO 并没有使用common mapper里的Example类
 * @author joshui
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
public class MybatisCommonMapperTest {
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Autowired
	CommonMapperService commonMapperService;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private GradeEventMapper gradeEventMapper;
	
	/* 可以使用这种方式，屏蔽掉工程里原有的@Configuration配置
	 * 
	 * @Configuration 
	// 使切面生效
	@EnableAspectJAutoProxy
	@Import({SampleDynamicDataSourceConfig.class})
	@tk.mybatis.spring.annotation.MapperScan(basePackages = "com.sky.mybatis.mybatisSpringBootCommonMapper")
	static class AppConfig {
	}*/
	
	/**
	 * 测试查询：主从数据源自动切换
	 * 有两种方式切换数据源：
	 * 动态：使用切面动态切换数据源，这也是本项目使用的方式
	 * 	定义一个数据源上下文holder静态类，在类里定义threadLocal变量，用于存放数据源名称
	 * 	定义一个动态DataSource，继承spring的AbstractRoutingDataSource，动态DataSource读取threadLocal变量里的数据源名称
	 * 	定义一个mybatis的sessionFactory，sessionFactory使用上面定义的动态DataSource
	 * 	定义数据源注解，注解使用到方法层面，决定使用哪个数据源，设置默认使用的数据源
	 * 	定义一个切面，关注点为自定义的数据源注解，在切面里根据注解里使用的数据源名称，动态改变threadLocal变量里的数据源名称
	 * 	未实测（在本项目没出现）：设置事务切面和动态数据源切面的执行顺序，不然会导致动态数据源切换不了。@EnableTransactionManagement(order = 2) 、@Order(1)
	 * 静态：定义多个mybatis sessionFactory，每个sessionFactory配置不同的数据源且扫描不同的mapper目录
	 */
	@Test(timeout = 1000 * 60)
	public void testDynamicQuery() {
		List<GradeEvent> selectAll = commonMapperService.selectGradeEvents();
		assertThat(selectAll.size()).isEqualTo(6);
		
		List<User> users = commonMapperService.selectUsers();
		assertThat(users.size()).isEqualTo(2);
		
		// 如果直接在mapper层面想切换数据库，是做不到的。貌似可以使用tk.mybatis.spring.mapper.MapperScannerConfigurer（而不是tk的MapperScan注解），在mapper层面做到动态切换
		expectedEx.expect(BadSqlGrammarException.class);
		gradeEventMapper.getGradeEventByPageParam(1, 1);
	}
	
	/**
	 * 测试自定义SQL
	 */
	@Test(timeout = 1000 * 60)
	public void testCustomizQuery() {
		List<User> selectAll = commonMapperService.findByCustomizeSQl();
		assertThat(selectAll.size()).isEqualTo(2);
		
		assertThat(selectAll.get(0).getId()).isEqualTo(selectAll.get(1).getId());
	}
	
	/**
	 * 测试分页查询
	 * 除了这里的方式之外，还有其他几种方式，具体参见官方文档
	 * 本项目使用了动态数据源，导致本方法测试不通过，会报“在系统中发现了多个分页插件，请检查系统配置!”，所以只能采用方法参数那种方式
	 * 测试不通过的原因，可能是动态数据源和分页插件都是用了threadlocal变量？
	 */
	@Test(expected = MyBatisSystemException.class, timeout = 1000 * 60)
//	@Ignore
	@SuppressWarnings("unused")
	public void testQueryPage() {
		// 方式1
		Page<User> page = PageHelper.startPage(1, 1).doSelectPage(()-> userMapper.getUserByPage());
		// 方式2
		PageInfo<Object> pageInfo = PageHelper.startPage(1, 1).doSelectPageInfo(()-> userMapper.getUserByPage());
		// 方式3
		long count = PageHelper.count(()-> userMapper.getUserByPage());
//		// 方式4
		PageHelper.startPage(1, 1);
		PageHelper.orderBy("id"); // 貌似默认是根据主键排序的？
		List<User> users = userMapper.getUserByPage();
		
		assertThat(users.size()).isEqualTo(1);
		assertThat(users.get(0).getName()).isEqualTo("admin");
	}
	
	@Test(timeout = 1000 * 60)
	public void testQueryPageParam() {
		List<User> users = userMapper.getUserByPageParam(1, 1);
		
		assertThat(users.size()).isEqualTo(1);
		assertThat(users.get(0).getName()).isEqualTo("common");
	}
}
