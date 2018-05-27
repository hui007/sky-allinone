package com.sky.allinone.security.conf;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.sky.allinone.mvc.conf.WebConfig;

/**
 * 方法级别的调用安全性，如果是web应用，则是web mvc安全性的补充，如果不是web应用，则是第一道防火墙
 * @author joshui
 *
 */
@Configuration
//@Import(WebConfig.class)
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
	/**
	 * 这个对象不能定义在webApplicationContext里，否则启动时，汇报需要servletContext的错误
	 */
	@Autowired
	UserDetailsService userDetailsService;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		/*
		 * 这里的逻辑是不是要跟WebSecurityConfig.configure(AuthenticationManagerBuilder)一样？
		 * 应该是可以不一样的
		 */
		auth.userDetailsService(userDetailsService);
		
	}
}
