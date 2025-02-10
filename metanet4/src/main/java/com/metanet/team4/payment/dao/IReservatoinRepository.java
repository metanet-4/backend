package com.metanet.team4.payment.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import com.metanet.team4.payment.model.Reservation;
import com.metanet.team4.payment.model.ReservationDetailDto;

@Mapper
public interface IReservatoinRepository {
	
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertReservation(Reservation reservation);
	
    ReservationDetailDto getReservationDetail(Long reservationId);
    
    Reservation getReervationByUserIdAndId(Long userId, Long reservationId);
    
    int updateTicketStatus(Long reservationId, Integer ticketStatus);
}
