package com.sky.allinone.security.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer.UserDetailsBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Configuration
//@EnableWebSecurity
//@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
//@EnableConfigurationProperties(SecuritySettings.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	@Autowired
	DataSource ds;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// 1、基于内存用户存储
		auth.inMemoryAuthentication().withUser("user").password("userpw").roles("USER")
			.and()
			.withUser("admin").password("adminpw").roles("USER", "ADMIN");
		
		// 2、基于数据库用户存储
//		auth.jdbcAuthentication().dataSource(ds)
//			.usersByUsernameQuery(JdbcUserDetailsManager.DEF_USERS_BY_USERNAME_QUERY)
//			.authoritiesByUsernameQuery(JdbcUserDetailsManager.DEF_AUTHORITIES_BY_USERNAME_QUERY)
//			// 可以使用自定义的密码加密/解密类
//			.passwordEncoder(new StandardPasswordEncoder("abcd"));
		
		// 3、基于LDAP服务器的用户存储。spring本身提供了内嵌的LDAP服务器
		
		// 4、使用自定义的userDetailsService
		// 也可以自定义一个域对象继承UserDetails
//		auth.userDetailsService(username -> new User(username, username, true, true, true, true, null));
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin() // 打开默认的登录验证功能
			.and()
			.authorizeRequests()
			// 除了ant风格之外，还可以使用正则表达式的风格
			.antMatchers("/getGradeEvents", "/getGradeEventsList").authenticated()
			// 规则会按照顺序发挥作用。所以具体的路径放前面，而不具体的路径放后面
			.antMatchers("/findByid/1").authenticated()
			.antMatchers("/findByid/**").permitAll()
			.antMatchers(HttpMethod.GET, "/handlerException").hasRole("ADMIN")
			// 使用spel表达式
			.antMatchers("/redirectBefore").access("hasRole('USER') and hasIpAddress('127.0.0.1')")
			.anyRequest().permitAll();
	}
}
