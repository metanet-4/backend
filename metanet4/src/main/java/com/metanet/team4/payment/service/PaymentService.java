package com.metanet.team4.payment.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.metanet.team4.member.model.Member;
import com.metanet.team4.payment.dao.IReservatoinRepository;
import com.metanet.team4.payment.model.PaymentRequestDto;
import com.metanet.team4.payment.model.PaymentResponseDto;
import com.metanet.team4.payment.model.Reservation;
import com.metanet.team4.ticket.dao.ISeatRepository;
import com.metanet.team4.ticket.model.Seat;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    @Value("${bootpay.api.application-id}")
    private String bootpayApplicationId;

    @Value("${bootpay.api.private-key}")
    private String bootpayPrivateKey;

    private final IReservatoinRepository reservationRepository;
    private final ISeatRepository seatRepository;

    public PaymentResponseDto processPayment(PaymentRequestDto request, Member member) {
        // 예약 정보 생성
        Reservation reservation = new Reservation();
        reservation.setReservationTime(new Date());
        reservation.setReservationCode(generateUniqueReservationCode());
        reservation.setPaymentAmount(request.getPaymentAmount());
        reservation.setReceiptId(request.getReceiptId());
        reservation.setTicketType(request.getTicketType());
        reservation.setTicketStatus(1);
        reservation.setMemberId(member.getId());
        reservation.setPlayingId(request.getPlayingId());

        reservationRepository.insertReservation(reservation);
        System.out.println("예매 정보 저장 완료");

        // 좌석 데이터 생성 및 저장
        List<Seat> seats = parseSeatNames(request.getSeatNames(), request.getPlayingId(), reservation.getId());
        if (!seats.isEmpty()) {
        	for(Seat seat : seats) {
                seatRepository.insertSeat(seat);
        	}
            System.out.println("좌석 정보 저장 완료: " + seats);
        }

        // 응답 생성
        PaymentResponseDto response = new PaymentResponseDto();
        response.setReceiptId(request.getReceiptId());
        response.setReservationId(reservation.getId());

        return response;
    }

    private static Long generateUniqueReservationCode() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
    }

    // 🎯 좌석 문자열을 파싱하여 Seat 객체 리스트로 변환
    private List<Seat> parseSeatNames(String seatNames, Long playingId, Long reservationId) {
        if (seatNames == null || seatNames.trim().isEmpty()) {
            return List.of();
        }

        return Arrays.stream(seatNames.split(","))
                .map(String::trim)
                .map(seatName -> {
                    Seat seat = new Seat();
                    seat.setName(seatName);
                    seat.setPlayingId(playingId);
                    seat.setReservationId(reservationId);
                    return seat;
                })
                .collect(Collectors.toList());
    }
}
