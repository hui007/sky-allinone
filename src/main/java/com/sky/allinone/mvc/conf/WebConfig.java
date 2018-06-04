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
	
	/*
	 * Spring mvc创建rest api。
	 * 表述：
	 * 任何给定的资源能以任意的表述形式返回给客户端，比如json、xml、html、atom、rss等。在spring里，就是同一套接口代码，能根据
	 * 情况返回不同的表述形式。
	 * spring提供的两种将资源的java表述形式转换为客户端需要的表述形式：
	 * 内容协商和消息转换器。
	 * 
	 * 协商资源表述：
	 * 逻辑视图名：方法返回逻辑视图名，否则，将根据请求url自动得出。得出逻辑视图名后，dispatchServlet会将视图名字传给一个视图解析器。
	 * 确定请求的媒体类型：先查看url的文件扩展名，否则，查看accept头部发送的值，否则，使用“/”作为默认的媒体类型。内容协商解析器会要求其他
	 * 的视图解析器解析视图，然后遍历得到的视图列表和客户端请求的所有媒体类型，匹配到的第一个视图用来渲染模型。
	 * 影响媒体类型的选择：spring3.2之前通过配置内容协商视图解析器，3.2之后通过配置视图协商管理器。
	 * 内容协商解析器的优劣：
	 * 适用场景：面向人类用户的接口和面向非人类用户的接口有很多重叠，也就是接口代码一套，表述形式多样；
	 * 渲染：选中的view会渲染模型给客户端，而不是资源。比如json格式，模型里有个map，map里有个key是个list<map>，会将map里的key作为json的根返回客户端。
	 * 
	 * http信息转换器：
	 * 过程：直接将控制器数据转换为表述形式，不存在模型，也没有视图。dispatchServlet不会那么麻烦的将模型数据传输到视图中。
	 * 自带的转换器：会自动注册很多转换器，如果没有自动注册的话，手动注册就好了。
	 * 如何选择转换器：可以通过accept头信息选择；可以通过具体引入的jar包确定，比如是选择json还是json2转换器，看具体引入的是哪种jar包。
	 */
}
