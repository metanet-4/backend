package com.metanet.team4.ticket.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.metanet.team4.ticket.dao.ITicketRepository;
import com.metanet.team4.ticket.dto.CinemaFindResponseDto;
import com.metanet.team4.ticket.dto.PlayingResponseDto;
import com.metanet.team4.ticket.dto.ScreenFindResponseDto;
import com.metanet.team4.ticket.dto.SeatResponseDto;
import com.metanet.team4.ticket.dto.TicketRequestDto;
import com.metanet.team4.ticket.dto.TicketResponseDto;
import com.metanet.team4.ticket.dto.TimeDto;

@Service
public class TicketService implements ITicketService{

	@Autowired
	ITicketRepository iTicketRepository;
	
	@Override
	public Map<String, List<CinemaFindResponseDto>> findCinemaList(String movieId) {
		List<CinemaFindResponseDto> cinemaList = iTicketRepository.findScreenListByMovieId(movieId); 
		Map<String, List<CinemaFindResponseDto>> groupByLocation = cinemaList.stream().collect(Collectors.groupingBy(CinemaFindResponseDto::getLocation));
		
		return groupByLocation;
	}

	@Override
	public Map<LocalDateTime, List<ScreenFindResponseDto>> findScreenList(Long cinemaId, String movieId) {
		List<ScreenFindResponseDto> screenList = iTicketRepository.findPlayingListByCinemaId(cinemaId, movieId);
		// 날짜를 기준으로 묶은 후 시간을 기준으로 묶어서 전달		
		//Map<Date, List<ScreenFindResponseDto>> groupByDate = screenList.stream().collect(Collectors.groupingBy(ScreenFindResponseDto::getPlayingDate));
		
		Map<LocalDateTime, List<ScreenFindResponseDto>> groupByDate = screenList.stream()
			    .filter(dto -> dto.getPlayingDate() != null) // null 값 제거
			    .collect(Collectors.groupingBy(ScreenFindResponseDto::getPlayingDate));
		
		return groupByDate;
	}
	
	@Override
	public List<SeatResponseDto> findSeatList(Long playingId) {
		List<SeatResponseDto> list = iTicketRepository.findSeatList(playingId);
		return list;
	}

	@Override
	public List<TicketResponseDto> getSeatInfo(TicketRequestDto ticketReqeustDto, List<TimeDto> timeList) {
		// DB 정보값들을 불러온 후에 TicketResponseDto에 담아서 보내야 한다.
		List<TicketResponseDto> ticketList = iTicketRepository.findReserveInfo(ticketReqeustDto.getPlayingId(), ticketReqeustDto.getScreenId());
		// 티켓에 해당하는 정보들은 프론트에서 넘겨줘야함
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
		
		ticketList.forEach(a -> timeList.stream().filter(b-> b.getId() == a.getPlayingId())
				.findFirst()
				.ifPresent(b -> a.setTimeInfo(
						String.valueOf(dateFormatter.format(b.getPlayingDate()) + " ("+timeFormatter.format(b.getStartTime()) + " ~ " + timeFormatter.format(b.getEndTime()) +")"))));
		return ticketList;
	}

	@Override
	public List<TimeDto> getTimeDtoList(Long id) {
		List<TimeDto> list = iTicketRepository.findPlayingTime(id);
		return list;
	}


    @Override
    public PlayingResponseDto findPlayingInfo(Long playingId) {
        return iTicketRepository.findPlayingInfo(playingId);
    }

}