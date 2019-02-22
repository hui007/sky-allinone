package com.sky.allinone.dao.entity;

import java.io.Serializable;

public abstract class BaseEntity implements Serializable{
	

	private static final long serialVersionUID = -607752621362896528L;

	public abstract Serializable getId();

}
