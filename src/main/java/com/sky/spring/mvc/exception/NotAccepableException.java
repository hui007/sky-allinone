package com.sky.spring.mvc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "不接受请求")
public class NotAccepableException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
