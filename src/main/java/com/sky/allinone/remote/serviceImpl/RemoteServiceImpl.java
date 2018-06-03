package com.sky.allinone.remote.serviceImpl;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import com.sky.allinone.remote.service.RemoteServiceInf;

@Service
public class RemoteServiceImpl implements RemoteServiceInf{

	@Override
	public void sayHi() {
		throw new RuntimeException("本HessianService方法待实现");
	}

	@Override
	public String wahaha() {
		return "娃哈哈，我要喝";
	}
	
}
