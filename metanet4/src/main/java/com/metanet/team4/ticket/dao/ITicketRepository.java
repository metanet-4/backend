package com.metanet.team4.ticket.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.metanet.team4.ticket.dto.CinemaFindResponseDto;
import com.metanet.team4.ticket.dto.PlayingResponseDto;
import com.metanet.team4.ticket.dto.ScreenFindResponseDto;
import com.metanet.team4.ticket.dto.SeatResponseDto;
import com.metanet.team4.ticket.dto.TicketResponseDto;
import com.metanet.team4.ticket.dto.TimeDto;

@Mapper
public interface ITicketRepository {
	
	// 해당하는 영화를 상영하는 영화관 정보를 가져오는 SQL
	@Select("SELECT distinct c.id, c.name, c.location "
			+ "FROM cinema c "
			+ "JOIN screen s ON c.id = s.cinema_id "
			+ "JOIN playing p ON s.id = p.screen_id "
			+ "JOIN movie m ON p.movie_id = m.id "
			+ "WHERE m.id = #{movieId}")
	List<CinemaFindResponseDto> findScreenListByMovieId(String movieId);
	

	// 상영 정보를 가져오는 SQL
	@Select("SELECT \n"
			+ "	   p.movie_id as movieId,\n"
			+ "    p.id AS playingId, \n"
			+ "    p.playing_date AS playingDate, \n"
			+ "    p.start_time AS startTime,\n"
			+ "    p.end_time AS endTime, \n"
			+ "    p.screen_id AS screenId, \n"
			+ "    (SELECT COUNT(*) FROM Seat s2 WHERE s2.playing_id = p.id) AS reservedSeat,\n"
			+ "    c.id AS cinemaId,\n"
			+ "    c.name AS cinemaName, \n"
			+ "    s.name AS screenName, \n"
			+ "    s.capacity AS capacity, \n"
			+ "    s.type AS type \n"
			+ "FROM cinema c\n"
			+ "JOIN screen s ON c.id = s.cinema_id\n"
			+ "JOIN playing p ON p.screen_id = s.id\n"
			+ "WHERE p.movie_id = #{movieId}\n"
			+ "  AND s.cinema_id = #{cinemaId}\n"
			+ "  AND p.playing_date BETWEEN TRUNC(SYSDATE) AND TRUNC(SYSDATE + 6)\n"
			+ "GROUP BY \n"
			+ "    p.id, p.playing_date, p.start_time, p.end_time, p.screen_id, p.movie_id,\n"
			+ "    c.id, c.name, s.name, s.capacity, s.type")
		List<ScreenFindResponseDto> findPlayingListByCinemaId(Long cinemaId, String movieId);
	
	// 좌석 정보 가져오는 SQL
	@Select("select"
			+ " s.id as seatId, "
			+ " s.name as name, "
			+ " p.id as playingId,"
			+ " m.krname as krName,"
			+ " m.watch_grade as watchGrade,"
			+ " p.start_time as startTime,"
			+ " c.name as cinemaName, "
			+ " sc.name as screenName,"
			+ " sc.type as type, "
			+ " m.id as movieId "
			+ "from seat s "
			+ "join playing p on p.id = s.playing_id "
			+ "join screen sc on sc.id = p.screen_id "
			+ "join movie m on m.id = p.movie_id "
			+ "join cinema c on c.id = sc.cinema_id "
			+ "where s.playing_id = #{playingId}")
	List<SeatResponseDto> findSeatList(Long playingId);
	
	// 상영 시간 정보 가져오는 SQL
	@Select("select "
			+ " id, "
			+ " playing_date as playingDate, "
			+ " start_time as startTime, "
			+ " end_time as endTime "
			+ "from playing "
			+ "where id = #{playingId}")
	List<TimeDto> findPlayingTime(Long playingId);
	
	
	// 예매를 위한 정보 가져오는 SQL
	// 티켓에 해당하는 정보들은 프론트에서 넘겨줘야함
	@Select("SELECT \n"
			+ "    m.krname AS krName,\n"
			+ "    m.main_image AS mainImage,\n"
			+ "    p.id AS playingId,\n"
			+ "    sc.name AS screenName,\n"
			+ "    sc.type AS screenType,\n"
			+ "    c.name AS cinemaName\n"
			+ "FROM movie m\n"
			+ "JOIN playing p ON p.movie_id = m.id \n"
			+ "JOIN screen sc ON sc.id = p.screen_id \n"
			+ "JOIN cinema c ON c.id = sc.cinema_id \n"
			+ "WHERE p.id = #{playingId}\n"
			+ "  AND sc.id = #{screenId}")
	List<TicketResponseDto> findReserveInfo(Long playingId, Long screenId);
	
    @Select("""
            SELECT 
                m.krName AS krName,
                m.watch_grade AS watchGrade,
                TO_CHAR(p.playing_date, 'YYYY-MM-DD') || ' (' || 
                TO_CHAR(p.start_time, 'HH24:MI') || ' ~ ' || 
                TO_CHAR(p.end_time, 'HH24:MI') || ')' AS playingTime,
                c.name AS cinemaName,
                sc.name AS screenName,
                m.main_image AS movieImg
            FROM playing p
            JOIN movie m ON p.movie_id = m.id
            JOIN screen sc ON p.screen_id = sc.id
            JOIN cinema c ON sc.cinema_id = c.id
            WHERE p.id = #{playingId}
        """)
        PlayingResponseDto findPlayingInfo(Long playingId);


}