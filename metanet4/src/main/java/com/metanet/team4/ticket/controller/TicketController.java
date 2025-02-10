package com.metanet.team4.ticket.controller;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.team4.ticket.dto.CinemaFindResponseDto;
import com.metanet.team4.ticket.dto.ScreenFindResponseDto;
import com.metanet.team4.ticket.dto.SeatResponseDto;
import com.metanet.team4.ticket.dto.TicketRequestDto;
import com.metanet.team4.ticket.dto.TicketResponseDto;
import com.metanet.team4.ticket.dto.TimeDto;
import com.metanet.team4.ticket.service.ITicketService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/ticket")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@Tag(name ="Ticket", description="Ticket Reservation API")
public class TicketController {

	@Autowired
	ITicketService ticketService;
	

	// 1. 영화 선택 후 영화관 선택 페이지로 이동하는 로직 
	@GetMapping("/cinema")
	@Operation(summary="영화관 조회", description="해당 영화를 상영하는 영화관을 조회")
	public ResponseEntity<Map<String, List<CinemaFindResponseDto>>> getCinemaList(@RequestParam String movieId){
		Map<String, List<CinemaFindResponseDto>> map = ticketService.findCinemaList(movieId);
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}
	
	
	// 2. 영화관 선택 후 상영관(날짜/시간) 선택 페이지로 이동하는 로직
	@GetMapping("/screen")
	@Operation(summary="상영관 조회", description="영화관의 상영관(시간/날짜)을 조회")
	public ResponseEntity<Map<LocalDateTime, List<ScreenFindResponseDto>>> getPlayingList(
			@RequestParam Long cinemaId, 
			@RequestParam String movieId){
		Map<LocalDateTime,List<ScreenFindResponseDto>> map = ticketService.findScreenList(cinemaId, movieId);
		return ResponseEntity.status(HttpStatus.OK).body(map);
		
	}
	
	// 3-1. 좌석 상태
	@GetMapping("/seats")
	public ResponseEntity<List<SeatResponseDto>> getSeatList(
			@RequestParam Long playingId){
		
		List<SeatResponseDto> list = ticketService.findSeatList(playingId);
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}
	
	// 3-2. 상영관 선택 후 좌석 선택하는 로직
	@GetMapping("/seats/info")
	public ResponseEntity<List<TicketResponseDto>> postSeatInfo(@RequestBody TicketRequestDto ticketRequestDto){
		List<TimeDto> timeList = ticketService.getTimeDtoList(ticketRequestDto.getPlayingId());
		List<TicketResponseDto> ticketList = ticketService.getSeatInfo(ticketRequestDto, timeList);
		return ResponseEntity.status(HttpStatus.OK).body(ticketList);
	}
	
}
