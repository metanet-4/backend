package com.metanet.team4.payment.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metanet.team4.exception.CustomException;
import com.metanet.team4.exception.NotFoundException;
import com.metanet.team4.member.model.Member;
import com.metanet.team4.payment.dao.IReservatoinRepository;
import com.metanet.team4.payment.model.PaymentRequestDto;
import com.metanet.team4.payment.model.PaymentResponseDto;
import com.metanet.team4.payment.model.Reservation;
import com.metanet.team4.ticket.dao.ISeatRepository;
import com.metanet.team4.ticket.model.Seat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    @Value("${bootpay.api.application-id}")
    private String bootpayApplicationId;

    @Value("${bootpay.api.private-key}")
    private String bootpayPrivateKey;

    private final IReservatoinRepository reservationRepository;
    private final ISeatRepository seatRepository;

    @Transactional
    public PaymentResponseDto processPayment(PaymentRequestDto request, Member member) {

        if (request == null || request.getPaymentAmount() == null || request.getReceiptId() == null ||
            request.getTicketType() == null || request.getPlayingId() == null) {
            log.warn("결제 요청 데이터 검증 실패: {}", request);
            throw new CustomException("결제 요청 데이터가 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
        }

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
        if (reservation.getId() == null) {
            log.error("예약 저장 실패: {}", reservation);
            throw new CustomException("예약 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("예매 정보 저장 완료: {}", reservation);

        // 좌석 데이터 생성 및 저장
        List<Seat> seats = parseSeatNames(request.getSeatNames(), request.getPlayingId(), reservation.getId());
        if (!seats.isEmpty()) {
            for (Seat seat : seats) {
                seatRepository.insertSeat(seat);
            }
            log.info("좌석 정보 저장 완료: {}", seats);
        } else {
            log.warn("등록된 좌석이 없음: seatNames={}", request.getSeatNames());
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
