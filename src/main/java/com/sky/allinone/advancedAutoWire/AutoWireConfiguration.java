package com.sky.allinone.advancedAutoWire;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.alibaba.druid.pool.DruidDataSource;
import com.sky.allinone.dao.conf.DruidSourceProperties;

@Configuration
@EnableConfigurationProperties(DruidSourceProperties.class)
@PropertySource(value = "classpath:com/sky/allinone/advancedAutoWire/MagicProperties.properties", 
	encoding="utf-8")
//@AutoConfigureAfter({ WebMvcAutoConfiguration.class}) 这种注解不会起作用，这种只对xxx-starter自动配置起作用
public class AutoWireConfiguration {
	@Autowired
	DruidSourceProperties druidSourceProperties;
	
	@Bean
	@Profile("dev")
	@Qualifier("embeddedDS")
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL)
				.addScript("db/database-schema.sql")
				.addScript("db/database-test-data.sql").build();
	}
	
	@Bean
	@Profile("product")
	@Qualifier("productDS")
	public DataSource dataSourceDruid() {
		DruidDataSource datasource = new DruidDataSource();
		datasource.setUrl(druidSourceProperties.getDbUrl());
		datasource.setUsername(druidSourceProperties.getUsername());
		datasource.setPassword(druidSourceProperties.getPassword());
		datasource.setDriverClassName(druidSourceProperties.getDriverClassName());

		return datasource;
	}
	
	@Bean
	@Conditional(MagicCondition.class)
	@Primary
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public MagicBean magicBean() {
		return new MagicBean();
	}
	
	@Bean
	@Conditional(MagicCondition.class)
	@MagicQualifier
	public MagicBean magicBeanQualifier() {
		MagicBean magicBean = new MagicBean();
		magicBean.setName("magicBeanQualifier");
		return magicBean;
	}
}
