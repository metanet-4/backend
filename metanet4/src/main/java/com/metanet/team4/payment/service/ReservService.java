package com.metanet.team4.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metanet.team4.exception.NotFoundException;
import com.metanet.team4.payment.dao.IReservatoinRepository;
import com.metanet.team4.payment.model.CancelResponseDto;
import com.metanet.team4.payment.model.Reservation;
import com.metanet.team4.payment.model.ReservationDetailDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservService {
	
	private final IReservatoinRepository repository;
	
	@Transactional
    public ReservationDetailDto getReservationDetail(Long userId, Long reservationId) {
        log.info("예약 상세 조회 요청: userId={}, reservationId={}", userId, reservationId);
        
		Reservation reservation = repository.getReservationByUserIdAndId(userId, reservationId);
        if (reservation == null) {
            log.warn("예약 정보 없음: userId={}, reservationId={}", userId, reservationId);
            throw new NotFoundException("해당 예약 정보를 찾을 수 없습니다. reservationId: " + reservationId);
        }

        ReservationDetailDto reservationDetailDto = repository.getReservationDetail(reservationId);
        reservationDetailDto.setSeatList(reservation.getSeatList());

        log.info("예약 상세 조회 완료: reservationId={}, seatCount={}", reservationId, reservationDetailDto.getSeatList().size());
        return reservationDetailDto;
    }
    
    @Transactional
    public CancelResponseDto cancelReservation(Long userId, Long reservationId) {
        log.info("예약 취소 요청: userId={}, reservationId={}", userId, reservationId);
    	
        Reservation reservation = repository.getReservationByUserIdAndId(userId, reservationId);
        if (reservation == null) {
            log.warn("취소할 예약 정보 없음: userId={}, reservationId={}", userId, reservationId);
            throw new NotFoundException("해당 예약 정보를 찾을 수 없습니다. reservationId: " + reservationId);
        }
    	
        int updatedRows = repository.updateTicketStatus(reservationId, 0);
        if (updatedRows == 0) {
            log.error("예약 취소 실패: reservationId={}", reservationId);
            throw new RuntimeException("예약 취소 처리 중 오류가 발생했습니다.");
        }

        log.info("예약 취소 성공: reservationId={}", reservationId);
        return new CancelResponseDto("CANCELED", "예매 취소 성공!");
    }
}
