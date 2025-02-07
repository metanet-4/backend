package com.metanet.team4.payment.model;

import lombok.Getter;

@Getter
public class PaymentRequestDto {
	
	private Long playingId;
	private String receiptId;
	
	private Integer paymentAmount;
    private String ticketType; // 티켓 유형 (일반, 청소년 등)
}
