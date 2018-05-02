package com.sky.spring.mvc.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.sky.spring.mvc.exception.CommonException;

/**
 * 所有的controller都会被这里的注解方法拦截。
 * 可以注解@ExceptionHandler、@InitBinder、@ModelAttribute
 * 
 * InitBinder：个性化入参绑定
 * ModelAttribute：注解在方法上时，先于所有的controller方法之星；也可以放入入参上
 * 
 * @author joshui
 *
 */
@ControllerAdvice
public class CommonControllerAdvice {
	@ExceptionHandler(CommonException.class) 
	public String handlerMethodException() {
		
		return "dealedCommonException";
	}
}
