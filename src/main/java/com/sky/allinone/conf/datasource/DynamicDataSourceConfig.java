package com.sky.allinone.conf.datasource;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageInterceptor;
import com.sky.allinone.mybatis.plugin.ExamplePlugin;

@Configuration
//@Import({SampleMasterDataSourceConfig.class, SampleClusterDataSourceConfig.class})
@PropertySource("classpath:/application-dataSource.properties")
@EnableConfigurationProperties(DruidSourceProperties.class)
@ComponentScan({"com.sky.mybatis.mybatisSpringBootCommonMapper", "com.sky.mybatis.mybatisMultiDataSource"})
public class DynamicDataSourceConfig {
	@Autowired
	DruidSourceProperties druidSourceProperties;
	
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
	
	@Bean
	@Primary
	public DataSourceTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	@Bean
	@Primary
	public SqlSessionFactory sqlSessionFactory()
			throws Exception {
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		
		// 必须得设置属性，完成插件的初始化过程。如果是xml配置的话，就可以省略这些了
		PageInterceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.put("supportMethodsArguments", "true"); // 启用方法参数功能，参见com.sky.allinone.dao.mapper.UserMapper.getUserByPageParam(int, int)
        properties.put("params", "pageNum=pageNumKey;pageSize=pageSizeKey;"); // 设置方法参数名称：覆盖默认的名称
        interceptor.setProperties(properties);
        sessionFactory.setPlugins(new Interceptor[]{interceptor, new ExamplePlugin()});
		
        // 可以使用这种方式，也可以在配置文件里配置。因为使用了动态数据源，禁用掉了DataSourceAutoConfiguration，貌似在配置文件里配置的就识别不了了
        sessionFactory.setTypeAliasesPackage("com.sky.allinone.dao.entity");
		sessionFactory
				.setMapperLocations(
						new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
		return sessionFactory.getObject();
	}
}