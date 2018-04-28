package com.sky.spring.mvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
// @EnableWebMvc 不加这个注解，可以使用springboot提供的spring mvc特性
// @ComponentScan("com.sky.spring.mvc.web")
public class WebConfig extends WebMvcConfigurerAdapter {
	/**
	 * 视图解析器
	 * 
	 * @return
	 */
	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
//		resolver.setPrefix("/WEB-INF/views/");
		resolver.setPrefix("/");
		resolver.setSuffix(".html");
		resolver.setExposeContextBeansAsAttributes(true);

		return resolver;
	}

	/*
	 * 静态资源处理。将静态资源转发到默认的servlet容器默认的servlet上
	 */
	// @Override
	// public void
	// configureDefaultServletHandling(DefaultServletHandlerConfigurer
	// configurer) {
	// configurer.enable();
	// }
}
