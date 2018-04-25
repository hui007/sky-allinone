package com.sky.movie.mybatisMultiDataSource.config.ds;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;

//@Configuration
//@PropertySource("classpath:/application-dataSource-multi.properties")
// 扫描 Mapper 接口并容器管理
//@MapperScan(basePackages = SampleMasterDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "masterSqlSessionFactory")
public class SampleMasterDataSourceConfig {

	// 精确到 master 目录，以便跟其他数据源隔离
	static final String PACKAGE = "com.sky.movie.mybatisMultiDataSource.data.master";
	// static final String MAPPER_LOCATION = "classpath:mapper/master/*.xml";

	@Value("${master.datasource.url}")
	private String url;

	@Value("${master.datasource.username}")
	private String user;

	@Value("${master.datasource.password}")
	private String password;

	@Value("${master.datasource.driverClassName}")
	private String driverClass;

	@Bean(name = "masterDataSource")
	public DataSource masterDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(driverClass);
		dataSource.setUrl(url);
		dataSource.setUsername(user);
		dataSource.setPassword(password);
		return dataSource;
	}

	@Bean(name = "masterTransactionManager")
//	@Primary
	public DataSourceTransactionManager masterTransactionManager() {
		return new DataSourceTransactionManager(masterDataSource());
	}

	@Bean(name = "masterSqlSessionFactory")
//	@Primary
	public SqlSessionFactory masterSqlSessionFactory(@Qualifier("masterDataSource") DataSource masterDataSource)
			throws Exception {
		final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setTypeAliasesPackage("com.sky.movie.mybatis.domain");
		sessionFactory.setDataSource(masterDataSource);
		// sessionFactory.setMapperLocations(new
		// PathMatchingResourcePatternResolver()
		// .getResources(MasterDataSourceConfig.MAPPER_LOCATION));
		return sessionFactory.getObject();
	}
	
	/**
     * 动态数据源: 通过AOP在不同数据源之间动态切换
     * @return
     */
//    @Bean(name = "dynamicDataSource")
//    public DataSource dataSource(@Qualifier("clusterDataSource") DataSource clusterDataSource) {
//        DynamicDataSource dynamicDataSource = new DynamicDataSource();
//        // 默认数据源
//        dynamicDataSource.setDefaultTargetDataSource(masterDataSource());
//
//        // 配置多数据源
//        Map<Object, Object> dsMap = new HashMap<Object, Object>();
//        dsMap.put("masterDataSource", masterDataSource());
//        dsMap.put("clusterDataSource", clusterDataSource);
//
//        dynamicDataSource.setTargetDataSources(dsMap);
//
//        return dynamicDataSource;
//    }
}