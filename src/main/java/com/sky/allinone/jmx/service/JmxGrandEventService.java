package com.sky.allinone.jmx.service;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

@Service
@ManagedResource(objectName = "com.sky.allinone:name=JmxGrandEventService")
public class JmxGrandEventService implements JmxGrandEventServiceInf{
	private String attr = "before";

	/**
	 * 暴露为托管属性，注解后，可以查看该属性。
	 * @return
	 */
	@ManagedAttribute
	public String getAttr() {
		return attr;
	}

	/**
	 * 暴露为托管属性，注解后，可以修改该属性
	 * @param attr
	 */
	@ManagedAttribute
	public void setAttr(String attr) {
		this.attr = attr;
	}
	
	/**
	 * 暴露为托管操作
	 * @return
	 */
	@ManagedOperation
	public String sayHi() {
		return "你好，我的朋友";
	}
}
