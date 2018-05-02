package com.sky.spring.mvc.config;

import javax.servlet.MultipartConfigElement;

import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@ServletComponentScan({"com.sky.spring.mvc"})
// @EnableWebMvc 不加这个注解，可以使用springboot提供的spring mvc特性
// @ComponentScan("com.sky.spring.mvc.web")
//@Import(WebMvcAutoConfiguration.class)
//@AutoConfigureAfter({ WebMvcAutoConfiguration.class,}) 这种注解不会起作用的，这种只对xxx-starter自动配置起作用
public class WebConfig extends WebMvcConfigurerAdapter {
//	@Autowired
//	private MultipartConfigElement multipartConfigElement;
//	@Autowired 注入不进来，因为WebMvcConfigurerAdapter优先于自动配置里的DispatcherServlet
//	private DispatcherServlet dispatcherServlet;
	
//	@Bean
	public ServletRegistrationBean apiServlet(DispatcherServlet dispatcherServlet) {
	    ServletRegistrationBean bean = new ServletRegistrationBean(dispatcherServlet);
	    //注入上传配置到自己注册的ServletRegistrationBean
//	    bean.addUrlMappings("/api/*");
//	    bean.setMultipartConfig(multipartConfigElement);
//	    bean.setName("apiServlet");
	    return bean;
	}
	
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
