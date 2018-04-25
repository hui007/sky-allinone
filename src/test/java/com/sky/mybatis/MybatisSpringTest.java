package com.sky.mybatis;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sky.mybatis.mybatis.data.StudentMapper;
import com.sky.mybatis.mybatis.data.UserMapper1;
import com.sky.mybatis.mybatis.domain.User;
import com.sky.mybatis.mybatis.plugin.ExamplePlugin;
import com.sky.mybatis.mybatis.service.BarService;

/**
 * 集成spring
 * @author joshui
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisSpringTest {
	@Autowired
	private org.springframework.core.env.Environment env;
	@Autowired
	private StudentMapper studentMapper;
	@Autowired
	BarService barService;

	@Test
	public void contextLoads() {
		
	}
	
	/**
	 * 测试查询
	 */
//	@Test
	public void testQuery() {
		Map<String, String> student = studentMapper.getStudent(1);
		System.out.println(student);
	}
	
	@Test
	public void testQuery1() {
//		Map<String, String> student = studentMapper.getStudent(1);
		System.out.println(barService.doSomeBusinessStuff(1));
	}

	/**
	 * 使用spring的Environment
	 * @throws IOException
	 */
	@Test
	public void sqlSessionFactoryWithoutXML() throws IOException {
		Properties properties = new Properties();
		properties.setProperty("driver", env.getProperty("mybatisSpring.driverClassName"));
		properties.setProperty("url", env.getProperty("mybatisSpring.url"));
		properties.setProperty("username", env.getProperty("mybatisSpring.username"));
		properties.setProperty("password", env.getProperty("mybatisSpring.password"));
		PooledDataSourceFactory pooledDataSourceFactory = new PooledDataSourceFactory();
		pooledDataSourceFactory.setProperties(properties);
		DataSource dataSource = pooledDataSourceFactory.getDataSource();

		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, dataSource);
		
		Configuration configuration = new Configuration(environment);
		configuration.getTypeAliasRegistry().registerAliases("com.sky.mybatis.mybatis"); // 全限定名缩写
		// 比较坑，如果跟UserMapper1同目录，有个UserMapper1.xml，如果xml文件的namespace跟java文件的全路径不一致，会报错wrong
		// namespace
		configuration.addMapper(UserMapper1.class);
		configuration.addInterceptor(new ExamplePlugin());
		
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);

		// 使用try-with-resources
		try(SqlSession session = sqlSessionFactory.openSession()){
			UserMapper1 mapper = session.getMapper(UserMapper1.class);
			User user = mapper.getUser(1);
			System.out.println(user.getId() + user.getName());
			user = mapper.selectUser(1);
			System.out.println(user.getId() + user.getName());
		}
	}
}
