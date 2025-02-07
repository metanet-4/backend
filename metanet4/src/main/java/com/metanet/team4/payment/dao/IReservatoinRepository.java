package com.metanet.team4.payment.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import com.metanet.team4.payment.model.Reservation;

@Mapper
public interface IReservatoinRepository {
	
	@Insert("insertReservation")
    @Options(useGeneratedKeys = true, keyProperty = "id") // ✅ 자동 ID 매핑
    void insertReservation(Reservation reservation);
	
}
