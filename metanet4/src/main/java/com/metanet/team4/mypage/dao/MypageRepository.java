package com.metanet.team4.mypage.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.metanet.team4.mypage.model.MypageMember;
import com.metanet.team4.mypage.model.ReserveList;

@Repository
public class MypageRepository implements IMypageRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private class MypageMemberMapper implements RowMapper<MypageMember>{
		@Override
		public MypageMember mapRow(ResultSet rs, int rowNum) throws SQLException {
			MypageMember mypageMember = new MypageMember();
			mypageMember.setUserId(rs.getString("user_id"));
			mypageMember.setName(rs.getString("name"));
			mypageMember.setEmail(rs.getString("email"));
			mypageMember.setBirthday(rs.getDate("birthday"));
			mypageMember.setImage(rs.getBytes("image"));
			mypageMember.setGender(rs.getInt("gender"));
			return mypageMember;
		}
	}
	
	private class ReserveListMapper implements RowMapper<ReserveList> {
	    @Override
	    public ReserveList mapRow(ResultSet rs, int rowNum) throws SQLException {
	        ReserveList reserveList = new ReserveList();

	        reserveList.setUserId(rs.getString("userId"));
	        reserveList.setTicketStatus(rs.getInt("ticketStatus"));
	        reserveList.setMovieTitle(rs.getString("movieTitle"));
	        reserveList.setMainImage(rs.getString("mainImage"));
	        reserveList.setStartTime(rs.getDate("startTime"));
	        reserveList.setCinemaName(rs.getString("cinemaName"));
	        reserveList.setScreenName(rs.getString("screenName"));
	        reserveList.setSeatName(rs.getString("seatName"));
	        reserveList.setReservationCode(rs.getInt("reservationCode"));
	        reserveList.setReservationTime(rs.getDate("reservationTime"));
	        reserveList.setPaymentAmount(rs.getInt("paymentAmount"));

	        return reserveList;
	    }
	}

	
	
	@Override
	public MypageMember getMypageMember(String memberId) {
		String sql="select user_id, name, email, birthday, image, gender "
				+ "from member where user_id=?";
		return jdbcTemplate.queryForObject(sql, new MypageMemberMapper(), memberId);
	}

	@Override
	public List<ReserveList> getReserveList(String memberId) {
		String sql = "SELECT "
				+ "    m.user_id AS userId, "
				+ "    r.ticket_status AS ticketStatus, "
				+ "    mv.krName AS movieTitle, "
				+ "    mv.main_image AS mainImage, "
				+ "    p.start_time AS startTime, "
				+ "    c.name AS cinemaName, "
				+ "    s.name AS screenName, "
				+ "    st.name AS seatName, "
				+ "    r.reservation_code AS reservationCode, "
				+ "    r.reservation_time AS reservationTime, "
				+ "    r.payment_amount AS paymentAmount "
				+ "FROM reservation r "
				+ "JOIN member m ON r.member_id = m.id "
				+ "JOIN playing p ON r.playing_id = p.id "
				+ "JOIN movie mv ON p.movie_id = mv.id "
				+ "JOIN screen s ON p.screen_id = s.id "
				+ "JOIN cinema c ON s.cinema_id = c.id "
				+ "JOIN seat st ON r.seat_id = st.id "
				+ "WHERE m.user_id = ? and r.ticket_status=1";
		return jdbcTemplate.query(sql, new ReserveListMapper(), memberId);
	}

	@Override
	public List<ReserveList> getCancelList(String memberId) {
		String sql = "SELECT "
				+ "    m.user_id AS userId, "
				+ "    r.ticket_status AS ticketStatus, "
				+ "    mv.krName AS movieTitle, "
				+ "    mv.main_image AS mainImage, "
				+ "    p.start_time AS startTime, "
				+ "    c.name AS cinemaName, "
				+ "    s.name AS screenName, "
				+ "    st.name AS seatName, "
				+ "    r.reservation_code AS reservationCode, "
				+ "    r.reservation_time AS reservationTime, "
				+ "    r.payment_amount AS paymentAmount "
				+ "FROM reservation r "
				+ "JOIN member m ON r.member_id = m.id "
				+ "JOIN playing p ON r.playing_id = p.id "
				+ "JOIN movie mv ON p.movie_id = mv.id "
				+ "JOIN screen s ON p.screen_id = s.id "
				+ "JOIN cinema c ON s.cinema_id = c.id "
				+ "JOIN seat st ON r.seat_id = st.id "
				+ "WHERE m.user_id = ? and r.ticket_status=0";
		return jdbcTemplate.query(sql, new ReserveListMapper(), memberId);
	}
	



}
