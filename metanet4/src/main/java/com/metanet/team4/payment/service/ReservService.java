package com.metanet.team4.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metanet.team4.payment.dao.IReservatoinRepository;
import com.metanet.team4.payment.model.CancelResponseDto;
import com.metanet.team4.payment.model.Reservation;
import com.metanet.team4.payment.model.ReservationDetailDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservService {
	
	private final IReservatoinRepository repository;
	
	@Transactional
    public ReservationDetailDto getReservationDetail(Long reservationId) {
        return repository.getReservationDetail(reservationId);
    }
    
    @Transactional
    public CancelResponseDto cancelReservation(Long userId, Long reservationId) {
    	
        Reservation reservation = repository.getReervationByUserIdAndId(userId, reservationId);
        if (reservation == null) {
            throw new RuntimeException("해당 예약 정보를 찾을 수 없습니다. reservationId: " + reservationId);
        }
    	
        int updatedRows = repository.updateTicketStatus(reservationId, 0);
        if (updatedRows == 0) {
            throw new RuntimeException("예약 취소 처리 중 오류가 발생했습니다.");
        }

        return new CancelResponseDto("CANCELED", "예매 취소 성공!");
    }
}
