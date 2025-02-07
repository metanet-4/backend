package com.metanet.team4.mypage.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReserveList {
	String userId;
	int ticketStatus;
	String movieTitle;
	String mainImage;
	Date startTime;
	String cinemaName;
	String screenName;
	String seatName;
	int reservationCode;
	Date reservationTime;
	int paymentAmount;
}
