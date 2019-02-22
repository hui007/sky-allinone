package com.sky.allinone.advancedAutoWire;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

public class MagicBean {
	private String name;
	private static AtomicInteger count = new AtomicInteger(0);
	
	@PostConstruct
	private void afterInit() {
		count.incrementAndGet();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AtomicInteger getCount() {
		return count;
	}

	public void setCount(AtomicInteger count) {
		MagicBean.count = count;
	}
}
