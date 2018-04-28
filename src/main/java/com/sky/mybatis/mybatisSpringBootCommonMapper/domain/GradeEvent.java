package com.sky.mybatis.mybatisSpringBootCommonMapper.domain;

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
	@NotNull
	@Size(min=1, max=10)
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
