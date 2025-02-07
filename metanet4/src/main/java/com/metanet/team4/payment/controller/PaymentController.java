package com.metanet.team4.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.team4.common.Login;
import com.metanet.team4.member.model.Member;
import com.metanet.team4.payment.model.CancelResponseDto;
import com.metanet.team4.payment.model.PaymentRequestDto;
import com.metanet.team4.payment.model.PaymentResponseDto;
import com.metanet.team4.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;
@RestController 
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 요청 API
     * @param request 결제 요청 DTO
     * @return 결제 승인 결과
     */
    @PostMapping
    public ResponseEntity<PaymentResponseDto> requestPayment(@RequestBody PaymentRequestDto request, @Login Member member) {
        PaymentResponseDto response = paymentService.processPayment(request, member);
        return ResponseEntity.ok(response);
    }

    /**
     * 결제 취소 API
     * @param receiptId 부트페이 영수증 ID
     * @return 취소 결과
     */
    @PostMapping("/cancel")
    public ResponseEntity<CancelResponseDto> cancelPayment(@RequestAttribute String receiptId) {
        CancelResponseDto response = paymentService.cancelPayment(receiptId);
        return ResponseEntity.ok(response);
    }
}
