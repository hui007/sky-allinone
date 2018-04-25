package com.sky.movie.mybatisSpringBootCommonMapper.domain;

import java.util.Date;

import javax.persistence.Id;

public class GradeEvent {
	@Id
	private String eventId;
	private Date date;
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
