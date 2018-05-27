package com.sky.allinone.mvc.conf;

import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.autoconfigure.web.MultipartProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import com.sky.allinone.security.conf.MethodSecurityConfig;

@Configuration
@ServletComponentScan({"com.sky.allinone.mvc.servlet"})
@EnableConfigurationProperties(MultipartProperties.class) // 文件上传
/*@EnableWebMvc 不加这个注解的话，可以使用springboot提供的spring mvc特性*/
public class WebConfig extends WebMvcConfigurerAdapter {
	/*
	 * 注入不进来，因为WebMvcConfigurerAdapter优先于自动配置里的DispatcherServlet
	@Autowired 
	private DispatcherServlet dispatcherServlet;
	*/
	
	/*
	 * 注意：以下内容可参考官方文档
	 * springboot 默认添加了ContentNegotiatingViewResolver和BeanNameViewResolver视图解析器；
	 * 在springboot里，内嵌的web容器默认是不支持jsp的，除非打成war包，发布到外部web容器，或者增加配置信息，告诉去哪里找到jsp页面；
	 */
	
	/**
	 * 视图解析器：不用定义这个视图解析器了，springboot默认已经定义过了
	 * 对于"/x/y.html"可以解析到x目录下的y.html文件
	 * controller：view name；
	 * viewResolver：根据view name解析为View对象
	 * view：接收model、httprequest、httpresponse
	 * @return
	 */
//	@Bean
	@Deprecated
	public ViewResolver internalResourceViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
//		resolver.setPrefix("/WEB-INF/views/");
		resolver.setPrefix("/");
		resolver.setSuffix(".jsp");
		resolver.setOrder(Integer.MIN_VALUE); // 这个视图解析器一般放到最后
		resolver.setContentType("text/html;charset=UTF-8");
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
	
//	@Bean
//	public ResourceHttpRequestHandler resourceHttpRequestHandler() {
//		ResourceHttpRequestHandler handler = new ResourceHttpRequestHandler();
//		handler.setSupportedMethods(HttpMethod.POST.name());
//		
//		return handler;
//	}
	
	/*
	 * TODO
	 * 1、设置静态资源放行规则：
	 * 	<mvc:resources mapping="/css/**" location="/css/"/> 、<mvc:resources mapping="/js/**" location="/js/"/>  
	 * 	或者所有静态资源放行，<mvc:default-servlet-handler />  
	 * 
	 */
}
