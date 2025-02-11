package com.metanet.team4.ticket.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.metanet.team4.ticket.dto.CinemaFindResponseDto;
import com.metanet.team4.ticket.dto.PlayingResponseDto;
import com.metanet.team4.ticket.dto.ScreenFindResponseDto;
import com.metanet.team4.ticket.dto.SeatResponseDto;
import com.metanet.team4.ticket.dto.TicketRequestDto;
import com.metanet.team4.ticket.dto.TicketResponseDto;
import com.metanet.team4.ticket.dto.TimeDto;

public interface ITicketService {
	Map<String, List<CinemaFindResponseDto>> findCinemaList(String movieId);
	Map<LocalDateTime, List<ScreenFindResponseDto>> findScreenList(Long cinemaId, String movieId);
	List<SeatResponseDto> findSeatList(Long playingId);
	List<TicketResponseDto> getSeatInfo(TicketRequestDto ticketReqeustDto, List<TimeDto> timeList);
	List<TimeDto> getTimeDtoList(Long id);
	PlayingResponseDto findPlayingInfo(Long playingId);
}
