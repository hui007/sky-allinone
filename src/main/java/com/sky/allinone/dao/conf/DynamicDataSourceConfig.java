package com.sky.allinone.dao.conf;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.io.VFS;
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
import com.sky.allinone.dao.plugin.ExamplePlugin;

import tk.mybatis.mapper.autoconfigure.SpringBootVFS;

@Configuration
//@Import({SampleMasterDataSourceConfig.class, SampleClusterDataSourceConfig.class})
@PropertySource("classpath:/application-dataSource.properties")
@EnableConfigurationProperties(DruidSourceProperties.class)
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
	
	/**
	 * spring事务不能回滚的情况：
	 * 1、servcie方法嵌套调用时；2、或者抛出检查异常时
	 * 解决办法：
	 * 针对1，可以拿到applicationContext或者aopContext里的自身的代理对象后，再继续调用嵌套的方法。
	 * 针对2，事务管理器默认只回滚未检查异常，可以声明式的指定哪些异常回滚，哪些异常不会滚。
	 * @return
	 */
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
		
        /*
         * 不加这句的话，在springboot打包成的jar里setTypeAliasesPackage设置的会不生效。
         * MyBatis扫描通过VFS来实现，在Spring Boot中，由于是嵌套Jar，导致Mybatis默认的VFS实现DefaultVFS无法扫描嵌套Jar中的类。
         */
        VFS.addImplClass(SpringBootVFS.class);
        // 可以使用这种方式，也可以在配置文件里配置。因为使用了动态数据源，禁用掉了DataSourceAutoConfiguration，貌似在配置文件里配置的就识别不了了
        sessionFactory.setTypeAliasesPackage("com.sky.allinone.dao.entity");
		sessionFactory
				.setMapperLocations(
						new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
		return sessionFactory.getObject();
	}
}