package com.sky.spring.mvc.config.spring;

import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * 使用servlet3.0规范，替代web.xml方式，也可以两者并存。 3.0规范在2009年就出来了，tomcat7或更高版本支持3.0规范。
 * 
 * 这里会启动两个应用上下文，一个是DispatcherServlet的，另一个是ContextLoaderListener的。
 * 
 * @author joshui
 *
 */
public class WebAppInitializer 
//extends AbstractAnnotationConfigDispatcherServletInitializer 先注释掉，以免跟spring boot的mvc冲突
{

//	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { RootConfig.class };
	}

//	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { WebConfig.class };
	}

//	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

}
