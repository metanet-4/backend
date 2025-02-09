package com.metanet.team4.mypage.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ReserveList {
	private String userId;
	private String movieId;
	private int ticketStatus;
	private String movieTitle;
	private String mainImage;
	private Date startTime;
	private String cinemaName;
	private String screenName;
	private String seatName;
	private int reservationCode;
	private Date reservationTime;
	private int paymentAmount;
}
