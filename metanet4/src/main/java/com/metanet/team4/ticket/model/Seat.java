package com.metanet.team4.ticket.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Seat {
	private Long id;
	private String name;
	private Long playingId;
	private Long reservationId;
}
