package com.sky.spring.mvc.config;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
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
		
//		PlatformResourceBundleLocator userResourceBundleLocator = new PlatformResourceBundleLocator("messages" );
//		Validator validator = Validation.byDefaultProvider()
//                .configure()
//                .messageInterpolator(new ResourceBundleMessageInterpolator(userResourceBundleLocator))
//                .buildValidatorFactory()
//                .getValidator();
		
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

	/*
	 * 静态资源处理。将静态资源转发到默认的servlet容器默认的servlet上。貌似在springboot里无需这样处理
	 */
	// @Override
	// public void
	// configureDefaultServletHandling(DefaultServletHandlerConfigurer
	// configurer) {
	// configurer.enable();
	// }
}
