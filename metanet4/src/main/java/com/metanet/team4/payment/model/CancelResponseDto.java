package com.metanet.team4.payment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CancelResponseDto {
    private String receiptId;
    private String status; // 취소 상태 (예: CANCELED, FAILED)
    private String message;
}
