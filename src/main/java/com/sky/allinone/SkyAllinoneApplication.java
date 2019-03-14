package com.sky.allinone;

import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;

/*
 *  需要设置事务(本质也是AOP)执行的顺序，否则事务的执行顺序高于后续的AOP，会导致动态切换数据源失效。
 *  同时需要设置动态切换数据源AOP的order(需要在事务之前，否则事务只发生在默认库中)  
 */
//@EnableTransactionManagement(order = 2) 

// 禁用掉默认的数据源获取方式，默认会读取配置文件的数据源（spring.datasource.*)。不禁用掉的话，动态数据源初始化的时候会报异常
@SpringBootApplication(exclude = {  
        DataSourceAutoConfiguration.class
})  
// 切面
@EnableAspectJAutoProxy
// 不使用mybatis的MapperScan。并且不能扫描到CustomBaseMapper，否则会报错
@tk.mybatis.spring.annotation.MapperScan(basePackages = "com.sky.allinone.dao.mapper")
// 引入elasticSearch
@EnableElasticsearchRepositories(basePackages = "com.sky.allinone.elasticsearch.repositories")
// 演示引入xml配置文件
@ImportResource(locations={"classpath:application-bean.xml"})
public class SkyAllinoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkyAllinoneApplication.class, args);
	}
	
	/* 
	 * remember-me功能，一定要使用UserDetailsService
	 * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#userDetailsService()
	 */
	@Bean
	public UserDetailsService userDetailsService() {
		return username -> {
			if (!"user".equals(username) && !"admin".equals(username)) {
				throw new BadCredentialsException("Username or password is not correct");
			}
			
			ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			String password = null;
			
			if ("user".equals(username)) {
				password = "userpw";
				authorities.add(() -> "userRole");
			}
			if ("admin".equals(username)) {
				authorities.add(() -> "adminRole");
				password = "adminpw";
			}
			
			if (StringUtils.isEmpty(password)) {
				throw new BadCredentialsException("Username or password is not correct");
			}
			
			return new User(username, password, true, true, true, true, authorities);
		};
	}
}