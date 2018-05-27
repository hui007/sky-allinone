package com.sky.allinone.service;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.sky.allinone.dao.entity.GradeEvent;

@Service
public class MethodSecurityService {
	@Secured("ROLE_userRole")
	public String secured() {
		return "/home";
	}
	
	/**
	 * 在方法调用之前验证
	 * @param event
	 * @return
	 */
	@PreAuthorize(value = "(hasRole('userRole') and #event.eventId.startsWith('5')) or hasRole('adminRole')")
	public String preAuthorize(GradeEvent event) {
		return "/home";
	}
	
	/**
	 * 在方法调用之后验证。
	 * 有时候只有返回结果后，才能做校验，此时这种后置校验就很有用；
	 * 需要注意方法调用时产生的副作用
	 * @param event
	 * @return
	 */
//	@PostAuthorize(value = "returnObject.category == principal.username") 不支持这种方式，可能是本项目引入的jar版本过低？
	@PostAuthorize(value = "returnObject == authentication.name")
	public String postAuthorize(String category) {
		GradeEvent event = new GradeEvent();
		event.setCategory(category);
		
		return category;
	}
}
