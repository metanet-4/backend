package com.metanet.team4.ticket.model;

import lombok.Getter;

@Getter
public class Screen {
	private Long id;
	private String name;
	private int capacity;
	private String type;
	private Long cinemaId;
}