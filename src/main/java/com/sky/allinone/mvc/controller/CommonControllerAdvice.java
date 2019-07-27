package com.sky.allinone.mvc.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sky.allinone.mvc.exception.CommonException;

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
@ControllerAdvice(basePackageClasses = RestfulController.class)
public class CommonControllerAdvice {
	@ExceptionHandler(CommonException.class) 
	public String handleMethodException(HttpServletRequest request, Throwable ex) {
		
		return "dealedCommonException";
	}
	
	@ExceptionHandler(BindException.class) 
	public String handleValidateException() {
		
		return "dealedCommonException";
	}
}
