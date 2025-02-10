package com.metanet.team4.ticket.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TicketResponseDto {
	private String krName;   // 영화 제목
    private String mainImage;   // 영화 이미지 URL
    
    private Long playingId;		 // 상영 Id
    
    private String screenName;   // 상영관 이름
    private String screenType;	 // 상영타입
    
    private String cinemaName;   // 영화관 이름

    private String timeInfo; 	 // 상영일 + 시작시간 + 종료시간
    
    private String seatName;     // 좌석 이름
    private String ticketType;   // 티켓 종류
    private int price;			 // 티켓 가격
}