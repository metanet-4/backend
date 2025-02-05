package com.metanet.team4.movie.dao;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.metanet.team4.movie.model.Movie;

@Repository
public class MovieRepository implements IMovieRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private class MovieMapper implements RowMapper<Movie>{
		@Override
		public Movie mapRow(ResultSet rs, int rowNum) throws SQLException {
			Movie movie = new Movie();
			movie.setId(rs.getString("id"));
			movie.setKrName(rs.getString("krname"));
			movie.setEnName(rs.getString("enname"));
			movie.setDirectors(rs.getString("directors"));
			movie.setActors(rs.getString("actors"));
			movie.setReleaseDate(rs.getDate("release_date"));
			movie.setOpenYn(rs.getString("open_yn"));
			movie.setMainImage(rs.getString("main_image"));
			movie.setDescription(rs.getString("description"));
			movie.setTotalAudience(rs.getInt("total_audience"));
			movie.setLikeCount(rs.getInt("like_count"));
			movie.setNation(rs.getString("nation"));
			movie.setShowTime(rs.getInt("show_time"));
			movie.setWatchGrade(rs.getString("watch_grade"));
			return movie;
		}
	}
	
	@Override
	public Movie SelectMovie(String id) {
		String sql="select id, krname, enname, directors, actors, release_date, open_yn, main_image, description, total_audience, like_count, nation, show_time, watch_grade\r\n"
				+ "from movie where id=?";
		return jdbcTemplate.queryForObject(sql, new MovieMapper(), id);
	}

}
