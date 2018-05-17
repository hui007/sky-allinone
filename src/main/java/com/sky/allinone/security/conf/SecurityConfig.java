package com.sky.allinone.security.conf;

import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer.UserDetailsBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.util.StringUtils;
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
	@Autowired
	private Environment env;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// 1、基于内存用户存储
//		auth.inMemoryAuthentication().withUser("user").password("userpw").roles("USER")
//			.and()
//			.withUser("admin").password("adminpw").roles("USER", "ADMIN");
		
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
		auth.userDetailsService(userDetailsService()); // 为了演示remember-me功能，使用这种方式。做测试的话，还是使用inMemoryAuthentication测试方便点
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.formLogin() // 如果没有重写本方法，默认是弹出模态框来登录的。formLogin()是通过页面表单的方式登录
//			.loginPage("/login.html") 可以直接写html页面路径，否则需要配置mvc.view.suffix
			.loginPage("/showLoginPage") // 登录页面
			/*
			 * spring security 3.x 默认的登录拦截URL是/j_spring_security_check,而spring security 4.x默认拦截的URL是/login，
			 * 在spring security中,具体处理表单登录验证的是o.s.s.w.a.UsernamePasswordAuthenticationFilter,
			 * 另外一个过滤器o.s.s.w.a.ui.DefaultLoginPageGeneratingFilter用于在没有指定登录页时动态生成一个默认登录页。
			 * 
			 * 最好还是不要自定义登录实现了，通过扩展默认登录filter或者直接使用默认登录filter，否则要自己写多很多代码。
			 * 使用默认登录的方式：在form里定义action为/xxx，在这里定义loginProcessingUrl为/xxx，这个/xxx不要定义在controller里
			 */
			.loginProcessingUrl("/login")
//			.loginProcessingUrl("/doLogin") // 登录接口。不要跟loginPage相同，否则会引发问题；在form里的action必须写成/doLogin/，否则会导致这个映射不生效，而使用默认的登录接口
			.defaultSuccessUrl("/welcomePage", false) // 登录成功
			.failureForwardUrl("/showLoginPagePost?myError=自定义登录失败url") // 登录失败。这个url映射必须是post映射，因为loginProcessingUrl是post请求，否则会报405错误
			.and()
			.logout() // 登出。spring security实现注销功能涉及的三个核心类为LogoutFilter,LogoutHandler,LogoutSuccessHandler
			.logoutUrl("/logout") // 这是默认的登录url
			.logoutSuccessUrl("/")
			.and()
			/*
			 * 参考springsecurity basic 认证，https://www.cnblogs.com/1xin1yi/p/7386122.html
			 * 可以通过“@AuthenticationPrincipal User loginedUser”，在登录方法里获取当前登录用户信息
			 */
			.httpBasic() // 因为开了formLogin()方式，basic认证方式貌似不起作用了。参考BasicAuthenticationFilter
			.and()
			.rememberMe()
			.tokenValiditySeconds(60*60*2) // 两小时有效期
			.key("md5-key")
//			.userDetailsService(userDetailsService())
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
			.anyRequest().permitAll()
			.and()
			.csrf().disable(); // 先去掉csrf，否则在html页面提交时，需要带上request parameter '_csrf' or header 'X-CSRF-TOKEN'，比较麻烦
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
			
			String password = null;
			if ("user".equals(username)) {
				password = "userpw";
			}
			if ("admin".equals(username)) {
				password = "adminpw";
			}
			
			if (StringUtils.isEmpty(password)) {
				throw new BadCredentialsException("Username or password is not correct");
			}
			
			return new User(username, password, true, true, true, true, new ArrayList<GrantedAuthority>());
		};
	}
}
