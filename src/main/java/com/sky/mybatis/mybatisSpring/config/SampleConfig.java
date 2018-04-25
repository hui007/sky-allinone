/**
 *    Copyright 2010-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.sky.mybatis.mybatisSpring.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;
import com.sky.mybatis.mybatis.data.UserMapper1;
import com.sky.mybatis.mybatisSpring.service.FooService;

@Configuration
//@PropertySource("classpath:/application-dataSource.properties")
@EnableConfigurationProperties(DruidSourceProperties.class)
public class SampleConfig {
	@Autowired
	DruidSourceProperties druidSourceProperties;
	@Autowired
	UserMapper1 userMapper;
	@Value("mybatisSpring.url.sampdb")
	String sampdbUrl;
	
//	@Bean
//	public DataSource dataSource() {
//		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL)
//				.addScript("org/mybatis/spring/sample/db/database-schema.sql")
//				.addScript("org/mybatis/spring/sample/db/database-test-data.sql").build();
//	}

	@Bean // 声明其为Bean实例
//	@Primary // 在同样的DataSource中，首先使用被标注的DataSource
	public DataSource dataSourceDruid() {
		DruidDataSource datasource = new DruidDataSource();

		datasource.setUrl(druidSourceProperties.getDbUrl());
		datasource.setUsername(druidSourceProperties.getUsername());
		datasource.setPassword(druidSourceProperties.getPassword());
		datasource.setDriverClassName(druidSourceProperties.getDriverClassName());

		// configuration
//		datasource.setInitialSize(initialSize);
//		datasource.setMinIdle(minIdle);
//		datasource.setMaxActive(maxActive);
//		datasource.setMaxWait(maxWait);
//		datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
//		datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
//		datasource.setValidationQuery(validationQuery);
//		datasource.setTestWhileIdle(testWhileIdle);
//		datasource.setTestOnBorrow(testOnBorrow);
//		datasource.setTestOnReturn(testOnReturn);
//		datasource.setPoolPreparedStatements(poolPreparedStatements);
//		datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
//
//		datasource.setFilters(filters);

		return datasource;
	}
	
	@Bean 
	@Qualifier("dataSourceDruidSampdb")
	public DataSource dataSourceDruidSampdb() {
		DruidDataSource datasource = new DruidDataSource();
		datasource.setUrl(druidSourceProperties.getDbUrl());
		datasource.setUsername(druidSourceProperties.getUsername());
		datasource.setPassword(druidSourceProperties.getPassword());
		datasource.setDriverClassName(druidSourceProperties.getDriverClassName());
		return datasource;
	}

	@Bean
	public PlatformTransactionManager transactionalManager() {
		return new DataSourceTransactionManager(dataSourceDruid());
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean ss = new SqlSessionFactoryBean();
		ss.setDataSource(dataSourceDruid());
		ss.setTypeAliasesPackage("com.sky.mybatis.mybatis.domain");
		ss.setMapperLocations(
				new Resource[] { new ClassPathResource("com/sky/mybatis/mybatis/data/UserMapper1.xml") });
		return ss.getObject();
	}

	@Bean
	public UserMapper1 userMapper() throws Exception {
		// when using javaconfig a template requires less lines than a
		// MapperFactoryBean
		SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory());
		return sqlSessionTemplate.getMapper(UserMapper1.class);
	}

	@Bean
	public UserMapper1 userMapperWithFactory() throws Exception {
		MapperFactoryBean<UserMapper1> mapperFactoryBean = new MapperFactoryBean<>();
		mapperFactoryBean.setMapperInterface(UserMapper1.class);
		mapperFactoryBean.setSqlSessionFactory(sqlSessionFactory());
		mapperFactoryBean.afterPropertiesSet();
		return mapperFactoryBean.getObject();
	}

	@Bean
	public FooService fooService() throws Exception {
		return new FooService(userMapper());
	}

	@Bean
	public FooService fooServiceWithMapperFactoryBean() throws Exception {
		return new FooService(userMapperWithFactory());
	}
	
	@Bean
	public FooService fooServiceMapperScan() throws Exception {
		return new FooService(userMapper);
	}

}
