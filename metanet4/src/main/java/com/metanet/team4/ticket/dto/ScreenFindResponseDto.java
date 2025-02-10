package com.metanet.team4.ticket.dto;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@Getter
public class ScreenFindResponseDto {
	private String movieId;
	
	private Long screenId;
	private String screenName;
	private int capacity;
	private String type;
	
	private Long cinemaId;
	private String cinemaName;
	
	private Long playingId;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime playingDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime startTime;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime endTime;
	
	private int reservedSeat;
}
