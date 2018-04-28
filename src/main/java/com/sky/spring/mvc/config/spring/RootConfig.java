package com.sky.spring.mvc.config.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Profile("testSpringMvc")
@Configuration
@ComponentScan(basePackages={"com.sky.spring.mvc"}, 
		excludeFilters = {@Filter(type=FilterType.ANNOTATION, value=EnableWebMvc.class)})
public class RootConfig {
	
}
