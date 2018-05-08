package com.sky.allinone.dao.entity;

import java.util.Date;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

public class GradeEvent {
	@NotNull
	@Id
	private String eventId;
	@DateTimeFormat(pattern="yyyyMMdd")
	private Date date;
	@NotNull(message="{gradeEvent.category.notNull}")
	@Size(min=1, max=10, message="{gradeEvent.category.size}")
//	@Size(min=1, max=10, message="category长度不对")
	private String category;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
}
