package com.sky.mybatis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Test;

import com.sky.mybatis.mybatis.data.StudentMapper;
import com.sky.mybatis.mybatis.data.UserMapper1;
import com.sky.mybatis.mybatis.domain.User;
import com.sky.mybatis.mybatis.plugin.ExamplePlugin;

/**
 * 不集成spring
 * 需要修改pom文件
 * @author joshui
 *
 */
public class MybatisAloneTest {
	
	/**
	 * 使用xml配置文件
	 * @throws IOException
	 */
	@Test
	public void sqlSessionFactoryXML() throws IOException {
		// 不是spring管理bean，就不能在配置文件里引用spirng的属性值了
		String resource = "com/sky/mybatis/mybatis/data/mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		// 可以注入properties属性文件，解析配置文件里的占位符
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, new Properties());

		SqlSession session = sqlSessionFactory.openSession();
		try {
			User user = (User) session.selectOne("com.sky.mybatis.mybatisSpring.domain.User.selectUser", 1);
			System.out.println(user.getId() + user.getName());
		} finally {
			session.close();
		}
	}
	
	/**
	 * 不使用xml配置文件，全部使用代码实现
	 * @throws IOException
	 */
	@Test
	public void sqlSessionFactoryWithoutXML() throws IOException {
		Properties properties = new Properties();
		properties.setProperty("driver", "com.mysql.jdbc.Driver");
		properties.setProperty("url", "jdbc:mysql://localhost:3306/bookshop?useUnicode=true&characterEncoding=utf-8");
		properties.setProperty("username", "bookshopadm");
		properties.setProperty("password", "bookshopdb");
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
	
	/**
	 * 测试多数据源
	 * @throws IOException
	 */
	@Test
	public void multiDatasource() throws IOException {
		String resource = "com/sky/mybatis/mybatis/data/mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		// 数据源1
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, new Properties());

		SqlSession session = sqlSessionFactory.openSession();
		try {
			User user = (User) session.selectOne("com.sky.mybatis.mybatisSpring.domain.User.selectUser", 1);
			System.out.println(user.getId() + user.getName());
		} finally {
			session.close();
		}
		
		InputStream inputStream1 = Resources.getResourceAsStream(resource);
		// 数据源2
		SqlSessionFactory sqlSessionFactory1 = new SqlSessionFactoryBuilder().build(inputStream1, "sampdb");
		SqlSession session1 = sqlSessionFactory1.openSession();
		try {
			StudentMapper mapper = session1.getMapper(StudentMapper.class);
			Map<String, String> student = mapper.getStudent(1);
			System.out.println(student);
		} finally {
			session.close();
		}
	}
}
