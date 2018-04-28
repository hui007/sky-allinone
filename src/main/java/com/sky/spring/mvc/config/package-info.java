/**
 * 集成spring的话，用com.sky.spring.mvc.config.spring里的配置。因为本项目是spring boot项目，所以不使用这里的配置。
 * 
 * If you want to keep Spring Boot MVC features, and you just want to add additional MVC configuration (interceptors, formatters, view controllers etc.) you can add your own @Configuration class of type WebMvcConfigurerAdapter, but without @EnableWebMvc. If you wish to provide custom instances of RequestMappingHandlerMapping, RequestMappingHandlerAdapter or ExceptionHandlerExceptionResolver you can declare a WebMvcRegistrationsAdapter instance providing such components.
 *
 * If you want to take complete control of Spring MVC, you can add your own @Configuration annotated with @EnableWebMvc.
 */
/**
 * 
 * @author joshui
 *
 */
package com.sky.spring.mvc.config;