package com.metanet.team4.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.team4.common.Login;
import com.metanet.team4.member.model.Member;
import com.metanet.team4.payment.model.CancelResponseDto;
import com.metanet.team4.payment.model.ReservationDetailDto;
import com.metanet.team4.payment.service.ReservService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
@Tag(name = "예매 관리", description = "티켓 예매 관련 API")
public class ReservController {

    private final ReservService reservService;

    @Operation(summary = "예매 상세 조회", description = "예매 ID를 기반으로 예매 상세 정보를 조회합니다.")
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDetailDto> getReservationDetail(
            @Parameter(description = "예매 ID", required = true) @PathVariable Long reservationId) {
        ReservationDetailDto response = reservService.getReservationDetail(reservationId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "예매 취소", description = "예매 ID를 입력하여 해당 예매를 취소합니다.")
    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<CancelResponseDto> cancelPayment(
            @Parameter(description = "예매 ID", required = true) @PathVariable Long reservationId,
            @Parameter(hidden = true) @Login Member member) {
        CancelResponseDto response = reservService.cancelReservation(member.getId(), reservationId);
        return ResponseEntity.ok(response);
    }
}
