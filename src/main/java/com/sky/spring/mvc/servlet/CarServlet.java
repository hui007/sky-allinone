package com.sky.spring.mvc.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.FrameworkServlet;

@SuppressWarnings("serial")
@WebServlet(urlPatterns={"/CarServlet"})
public class CarServlet  extends FrameworkServlet{

	@Override
	protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("使用@WebServlet定义的servlet");
	}

}
