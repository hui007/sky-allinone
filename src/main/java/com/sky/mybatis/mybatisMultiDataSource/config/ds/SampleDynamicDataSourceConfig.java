package com.sky.mybatis.mybatisMultiDataSource.config.ds;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageInterceptor;

@Configuration
//@Import({SampleMasterDataSourceConfig.class, SampleClusterDataSourceConfig.class})
@PropertySource("classpath:/application-dataSource.properties")
@ComponentScan({"com.sky.mybatis.mybatisSpringBootCommonMapper", "com.sky.mybatis.mybatisMultiDataSource"})
public class SampleDynamicDataSourceConfig {
//	@Autowired
//	@Qualifier("masterDataSource")
//	private DataSource masterDataSource;
//	@Autowired
//	@Qualifier("clusterDataSource")
//	private DataSource clusterDataSource;
//	@Autowired
//	private SampleMasterDataSourceConfig sampleMasterDataSourceConfig;
//	@Autowired
//	private SampleClusterDataSourceConfig sampleClusterDataSourceConfig;
	
	@Value("${master.datasource.url}")
	private String url;

	@Value("${master.datasource.username}")
	private String user;

	@Value("${master.datasource.password}")
	private String password;

	@Value("${datasource.driverClassName}")
	private String driverClass;
	
	@Value("${cluster.datasource.url}")
	private String url1;

	@Value("${cluster.datasource.username}")
	private String user1;

	@Value("${cluster.datasource.password}")
	private String password1;

	@Value("${datasource.driverClassName}")
	private String driverClass1;

	@Bean(name = "clusterDataSource")
//	@ConfigurationProperties(prefix = "datasource.master") 
	public DataSource clusterDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(driverClass1);
		dataSource.setUrl(url1);
		dataSource.setUsername(user1);
		dataSource.setPassword(password1);
		return dataSource;
	}

	@Bean(name = "masterDataSource")
//	@Primary
	public DataSource masterDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(driverClass);
		dataSource.setUrl(url);
		dataSource.setUsername(user);
		dataSource.setPassword(password);
		return dataSource;
	}
	
	@Bean(name = "dynamicTransactionManager")
	@Primary
	public DataSourceTransactionManager masterTransactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	@Bean(name = "dynamicSqlSessionFactory")
	@Primary
	public SqlSessionFactory masterSqlSessionFactory()
			throws Exception {
		final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		// 最好在spring配置文件里配置，这样所有的SqlSessionFactory都可能可以共享
//		sessionFactory.setPlugins(new Interceptor[]{new PageInterceptor()});
		sessionFactory.setTypeAliasesPackage("com.sky.mybatis.mybatis.domain");
		sessionFactory.setDataSource(dataSource());
		
		// 必须的设置属性，完成插件的初始化过程。如果是xml配置的话，就可以省略这些了
		PageInterceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        interceptor.setProperties(properties);
        sessionFactory.setPlugins(new Interceptor[]{interceptor});
		
		// sessionFactory.setMapperLocations(new
		// PathMatchingResourcePatternResolver()
		// .getResources(MasterDataSourceConfig.MAPPER_LOCATION));
		return sessionFactory.getObject();
	}
	
	/**
     * 动态数据源: 通过AOP在不同数据源之间动态切换
     * @return
     */
    @Bean(name = "dynamicDataSource")
    @Primary
    public DataSource dataSource(
//    		@Qualifier("masterDataSource") DataSource masterDataSource, 
//    		@Qualifier("clusterDataSource") DataSource clusterDataSource
    		) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        // 默认数据源
        dynamicDataSource.setDefaultTargetDataSource(masterDataSource());

        // 配置多数据源
        Map<Object, Object> dsMap = new HashMap<Object, Object>();
        dsMap.put("masterDataSource", masterDataSource());
        dsMap.put("clusterDataSource", clusterDataSource());

        dynamicDataSource.setTargetDataSources(dsMap);

        return dynamicDataSource;
    }
}