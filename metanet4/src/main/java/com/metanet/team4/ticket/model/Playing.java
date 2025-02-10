package com.metanet.team4.ticket.model;

import java.sql.Date;

import lombok.Getter;

@Getter
public class Playing {
	// 상영 : 영화의 상영 정보
	private String movieName;
	private Long screenId;
	private Date screeningDate;
	private Date startTime;
	private Date endTime;
}
