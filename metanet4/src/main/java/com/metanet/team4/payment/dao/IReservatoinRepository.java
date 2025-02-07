package com.metanet.team4.payment.dao;

import org.apache.ibatis.annotations.Mapper;

import com.metanet.team4.payment.model.Reservation;

@Mapper
public interface IReservatoinRepository {
    void insertReservation(Reservation reservation);
	
}
