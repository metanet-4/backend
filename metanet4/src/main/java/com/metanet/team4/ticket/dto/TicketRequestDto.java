package com.metanet.team4.ticket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketRequestDto {
	private String movieId;
	private Long playingId;
	private Long screenId;
	private String seatName;
}

