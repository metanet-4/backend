package com.metanet.team4.payment.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentRequestDto {

    @NotNull(message = "상영 ID는 필수 값입니다.")
    private Long playingId;

    @NotBlank(message = "영수증 ID는 필수 값입니다.")
    private String receiptId;

    @NotNull(message = "결제 금액은 필수 값입니다.")
    private Integer paymentAmount;

    @NotBlank(message = "티켓 유형은 필수 값입니다.")
    private String ticketType;

    @NotBlank(message = "좌석 정보는 필수 값입니다.")
    private String seatNames;
}
