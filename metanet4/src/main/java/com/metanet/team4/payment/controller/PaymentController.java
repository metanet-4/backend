package com.metanet.team4.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.team4.common.Login;
import com.metanet.team4.member.model.Member;
import com.metanet.team4.payment.model.PaymentRequestDto;
import com.metanet.team4.payment.model.PaymentResponseDto;
import com.metanet.team4.payment.service.PaymentService;
import com.metanet.team4.websocket.WebSocketService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Tag(name = "결제 관리", description = "결제 요청 및 승인 관련 API")
public class PaymentController {

    private final PaymentService paymentService;
    private final WebSocketService webSocketService;

    @Operation(summary = "결제 요청", description = "사용자가 결제를 요청하면 승인 결과를 반환합니다.")
    @PostMapping
    public ResponseEntity<PaymentResponseDto> requestPayment(
            @Parameter(description = "결제 요청 정보", required = true) @RequestBody PaymentRequestDto request,
            @Parameter(hidden = true) @Login Member member) {
    	PaymentResponseDto payment = paymentService.processPayment(request, member); 
    	if(payment != null) {
    		String message = member.getUserId() + "님 예매가 완료되었습니다.";
     		webSocketService.sendNotificationToUser(member.getUserId(), message);
    	}
    	
        return ResponseEntity.ok(payment);
    }
}

