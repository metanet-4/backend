package com.metanet.team4.payment.service;

import org.springframework.stereotype.Service;

import com.metanet.team4.payment.dao.IReservatoinRepository;
import com.metanet.team4.payment.model.ReservationDetailDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservService {
	
	private final IReservatoinRepository repository;
	
    public ReservationDetailDto getReservationDetail(Long reservationId) {
        return repository.getReservationDetail(reservationId);
    }
    
}
