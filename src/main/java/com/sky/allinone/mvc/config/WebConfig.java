package com.sky.allinone.mvc.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.autoconfigure.web.MultipartProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@ServletComponentScan({"com.sky.allinone.mvc.servlet"})
@EnableConfigurationProperties(MultipartProperties.class) // 文件上传
/*@EnableWebMvc 不加这个注解的话，可以使用springboot提供的spring mvc特性*/
public class WebConfig extends WebMvcConfigurerAdapter {
	/*
	 * 注入不进来，因为WebMvcConfigurerAdapter优先于自动配置里的DispatcherServlet
	@Autowired 
	private DispatcherServlet dispatcherServlet;*/
	
	/**
	 * 视图解析器
	 * 对于"/x/y.html"可以解析到x目录下的y.html文件
	 * controller：view name；
	 * viewResolver：根据view name解析为View对象
	 * view：接收model、httprequest、httpresponse
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
	
	/**
	 * 读取资源文件。用于spring mvc字段校验不通过的提示信息
	 * @return
	 */
	@Bean
	public LocalValidatorFactoryBean validatorFactory() {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.setProviderClass(HibernateValidator.class);
		localValidatorFactoryBean.setValidationMessageSource(reloadableResourceBundleMessageSource());
		
		return localValidatorFactoryBean;
	}
	
	@Bean
	public MessageSource reloadableResourceBundleMessageSource() {
		ReloadableResourceBundleMessageSource resource = new ReloadableResourceBundleMessageSource();
		resource.setBasename("classpath:messages");
		resource.setDefaultEncoding("utf-8");
		resource.setCacheSeconds(120);
		
		return resource;
	}
}
