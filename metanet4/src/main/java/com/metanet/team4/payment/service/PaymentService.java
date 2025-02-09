package com.metanet.team4.payment.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metanet.team4.member.model.Member;
import com.metanet.team4.payment.dao.IReservatoinRepository;
import com.metanet.team4.payment.model.CancelResponseDto;
import com.metanet.team4.payment.model.PaymentRequestDto;
import com.metanet.team4.payment.model.PaymentResponseDto;
import com.metanet.team4.payment.model.Reservation;
import com.metanet.team4.payment.model.ReservationDetailDto;

import lombok.RequiredArgsConstructor;


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
    	
    	System.out.println("예매 정보 저장");
    	
        PaymentResponseDto response = new PaymentResponseDto();
        response.setReceiptId("test-receipt-id"); // 실제 부트페이 API를 사용하면 여기서 받은 영수증 ID를 저장
        response.setStatus("SUCCESS");
        response.setReservationId(reservation.getId());

        return response;
    }

    private static Long generateUniqueReservationCode() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
    }
    
}
