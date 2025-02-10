package com.metanet.team4.ticket.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.metanet.team4.ticket.model.Seat;

@Mapper
public interface ISeatRepository {

	void insertSeat(Seat seats);
}
