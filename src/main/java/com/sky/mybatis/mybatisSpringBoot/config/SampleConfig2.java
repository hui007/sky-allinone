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
package com.sky.mybatis.mybatisSpringBoot.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInterceptor;
import com.sky.mybatis.mybatisSpring.config.DruidSourceProperties;

//@SpringBootConfiguration
/* 最好不要在同一个应用里，定义两个@SpringBootApplication，会导致应用被加载两次、配置相互冲突等问题
 * 因为外部SpringBootApplication去掉了DataSourceAutoConfiguration，原本不需要定义任何其他的数据库bean的，这里也需要定义了
 */
//@SpringBootApplication(exclude = {  
//        DataSourceAutoConfiguration.class  
//})
@Configuration
//@ComponentScan(basePackages = "com.sky.mybatis")
//@MapperScan("com.sky.mybatis.mybatisSpringBoot.data")
@MapperScan("com.sky.mybatis")
@EnableConfigurationProperties(DruidSourceProperties.class)

public class SampleConfig2 {
	@Autowired
	DruidSourceProperties druidSourceProperties;
//
	@Bean // 声明其为Bean实例
//	@Primary // 在同样的DataSource中，首先使用被标注的DataSource
	public DataSource dataSourceDruid() {
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
		
		// 必须的设置属性，完成插件的初始化过程。如果是xml配置的话，就可以省略这些了
		PageInterceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
//        properties.setProperty("helperDialect", helperDialect);
//        properties.setProperty("offsetAsPageNum", offsetAsPageNum);
        interceptor.setProperties(properties);
        ss.setPlugins(new Interceptor[]{interceptor});
		
		ss.setTypeAliasesPackage("com.sky.mybatis.mybatis.domain");
		ss.setMapperLocations(
				new Resource[] { new ClassPathResource("com/sky/mybatis/mybatis/data/UserMapper1.xml") });
		return ss.getObject();
	}

}
