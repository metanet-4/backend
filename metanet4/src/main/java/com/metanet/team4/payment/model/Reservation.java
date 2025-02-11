package com.metanet.team4.payment.model;

import java.util.Date;
import java.util.List;

import com.metanet.team4.ticket.model.Seat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString(exclude = {"receiptId", "qrcode"})
public class Reservation {
	private Long id;
 
    private Date reservationTime;
    private Long reservationCode;
    private Integer paymentAmount;
    
    private String ticketType;	  // 티켓유형(일반, 청소년, 우대)
    private Integer ticketStatus; // 티켓상태(예매, 취소)
    private String receiptId;
//    private String qrcode;
    
    private Long memberId;	// 멤버ID
    private Long playingId; // 상영ID
    private List<Seat> seatList;
    
}
