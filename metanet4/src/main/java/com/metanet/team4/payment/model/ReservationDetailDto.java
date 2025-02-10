package com.metanet.team4.payment.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReservationDetailDto {

	private String watchGrade;
	private String krName;
	private Date playingDate;
	private Date startTime;
	private Date endTime;
	private String cinemaName; // 영화관이름
	private String screenName; // 상영관 이름
	private String seatName;  // 좌석 이름
	private String ticketType; // 티켓 유형
	private Long reservationCode; // 예매 코드

}
