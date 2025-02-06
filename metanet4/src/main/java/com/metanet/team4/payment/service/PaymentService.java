package com.metanet.team4.payment.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.metanet.team4.member.model.Member;
import com.metanet.team4.payment.dao.IReservatoinRepository;
import com.metanet.team4.payment.model.CancelResponseDto;
import com.metanet.team4.payment.model.PaymentRequestDto;
import com.metanet.team4.payment.model.PaymentResponseDto;
import com.metanet.team4.payment.model.Reservation;

import lombok.RequiredArgsConstructor;

//import kr.co.bootpay.javaApache.Bootpay;

@Service
@RequiredArgsConstructor
public class PaymentService {
	
    @Value("${bootpay.api.application-id}")
    private String bootpayApplicationId;

    @Value("${bootpay.api.private-key}")
    private String bootpayPrivateKey;

    private final IReservatoinRepository reservationRepository;

    public PaymentResponseDto processPayment(PaymentRequestDto request, Member member) {
    	
    	Reservation reservation = new Reservation();
    	reservation.setReservationTime(new Date());
    	reservation.setReservationCode(generateUniqueReservationCode());
    	
    	reservation.setPaymentAmount(request.getPaymentAmount());
    	reservation.setReceiptId(request.getReceiptId());
    	reservation.setTicketType(request.getTicketType());
    	reservation.setTicketStatus(1);
    	
    	reservation.setMemberId(member.getId());
    	reservation.setPlayingId(request.getPlayingId());
    	reservation.setSeatId(1L); // todo 생성하고 넣는걸로 수정 해야함
    	
    	reservationRepository.insertReservation(reservation);
    	
        PaymentResponseDto response = new PaymentResponseDto();
        response.setReceiptId("test-receipt-id"); // 실제 부트페이 API를 사용하면 여기서 받은 영수증 ID를 저장
        response.setStatus("SUCCESS");

        return response;
    }

    private static Long generateUniqueReservationCode() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
    }
    
    
    public CancelResponseDto cancelPayment(String receiptId) {
//    	try {
//            Bootpay bootpay = new Bootpay(bootpayApplicationId, bootpayPrivateKey);
//            HashMap<String, Object> token = bootpay.getAccessToken();
//
//            if (token.get("error_code") != null) { // 토큰 발급 실패
//                throw new RuntimeException("부트페이 인증 토큰 발급 실패: " + token);
//            }
//
//            // 결제 취소 요청 객체 생성
//            Cancel cancel = new Cancel();
//            cancel.receiptId = receiptId;
//            cancel.cancelUsername = "관리자"; // 필요에 따라 동적으로 변경 가능
//            cancel.cancelMessage = "사용자 요청에 의한 결제 취소";
//
//            // 부트페이 API 호출
//            HashMap<String, Object> response = bootpay.receiptCancel(cancel);
//
//            if (response.get("error_code") == null) { // 성공
//                return new CancelResponseDto(receiptId, "CANCELED", "결제가 성공적으로 취소되었습니다.");
//            } else { // 실패
//                return new CancelResponseDto(receiptId, "FAILED", "결제 취소 실패: " + response.get("message"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new CancelResponseDto(receiptId, "FAILED", "결제 취소 중 예외 발생: " + e.getMessage());
//        }
    	return null;
    }
}
