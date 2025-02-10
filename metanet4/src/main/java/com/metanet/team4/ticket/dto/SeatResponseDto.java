package com.metanet.team4.ticket.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@Getter
public class SeatResponseDto {
	private Long seatId;
	private String name;
	
	private String playingId;
	private String krName;
	private String watchGrade;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime startTime;
	
	private String cinemaName;
	
	private String screenName;
	private String type;
}
