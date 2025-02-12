package com.metanet.team4.payment.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponseDto {

    private String receiptId; 
    private String status; // 결제 상태 (예: SUCCESS, FAILED)
    private Integer paidAmount; // 실제 결제된 금액
    private Long reservationId;
    private String movidName;
}
