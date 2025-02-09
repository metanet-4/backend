package com.metanet.team4.movie.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.metanet.team4.movie.model.Movie;
import com.metanet.team4.movie.model.MovieMemberForChart;

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
	
	private class MovieMemberForChartMapper implements RowMapper<MovieMemberForChart>{
		@Override
		public MovieMemberForChart mapRow(ResultSet rs, int rowNum) throws SQLException {
			MovieMemberForChart movie = new MovieMemberForChart();
			movie.setMovieId(rs.getString("movie_id"));
			movie.setMan(rs.getInt("man"));
			movie.setWoman(rs.getInt("woman"));
			movie.setAge10th(rs.getInt("age10th"));
			movie.setAge20th(rs.getInt("age20th"));
			movie.setAge30th(rs.getInt("age30th"));
			movie.setAge40th(rs.getInt("age40th"));
			movie.setAge50th(rs.getInt("age50th"));
			movie.setAge60th(rs.getInt("age60th"));
			movie.setAge70th(rs.getInt("age70th"));
			movie.setAge80th(rs.getInt("age80th"));
			return movie;
		}
	}
	
	@Override
	public Movie selectMovie(String id) {
		String sql="select id, krname, enname, directors, actors, release_date, open_yn, main_image, description, total_audience, like_count, nation, show_time, watch_grade "
				+ "from movie where id=?";
		return jdbcTemplate.queryForObject(sql, new MovieMapper(), id);
	}

	@Override
	public MovieMemberForChart countForChart(String id) {
		String sql="SELECT "
				+ "    p.movie_id, "
				+ "		NVL(COUNT(CASE WHEN m.gender = 1 THEN 1 END), 0) AS man, "
				+ "    NVL(COUNT(CASE WHEN m.gender = 0 THEN 1 END), 0) AS woman, "
				+ "    NVL(COUNT(CASE WHEN EXTRACT(YEAR FROM SYSDATE) - EXTRACT(YEAR FROM m.birthday) BETWEEN 10 AND 19 THEN 1 END), 0) AS age10th, "
				+ "    NVL(COUNT(CASE WHEN EXTRACT(YEAR FROM SYSDATE) - EXTRACT(YEAR FROM m.birthday) BETWEEN 20 AND 29 THEN 1 END), 0) AS age20th, "
				+ "    NVL(COUNT(CASE WHEN EXTRACT(YEAR FROM SYSDATE) - EXTRACT(YEAR FROM m.birthday) BETWEEN 30 AND 39 THEN 1 END), 0) AS age30th, "
				+ "    NVL(COUNT(CASE WHEN EXTRACT(YEAR FROM SYSDATE) - EXTRACT(YEAR FROM m.birthday) BETWEEN 40 AND 49 THEN 1 END), 0) AS age40th, "
				+ "    NVL(COUNT(CASE WHEN EXTRACT(YEAR FROM SYSDATE) - EXTRACT(YEAR FROM m.birthday) BETWEEN 50 AND 59 THEN 1 END), 0) AS age50th, "
				+ "    NVL(COUNT(CASE WHEN EXTRACT(YEAR FROM SYSDATE) - EXTRACT(YEAR FROM m.birthday) BETWEEN 60 AND 69 THEN 1 END), 0) AS age60th, "
				+ "    NVL(COUNT(CASE WHEN EXTRACT(YEAR FROM SYSDATE) - EXTRACT(YEAR FROM m.birthday) BETWEEN 70 AND 79 THEN 1 END), 0) AS age70th, "
				+ "    NVL(COUNT(CASE WHEN EXTRACT(YEAR FROM SYSDATE) - EXTRACT(YEAR FROM m.birthday) BETWEEN 80 AND 89 THEN 1 END), 0) AS age80th "
				+ "FROM playing p "
				+ "JOIN reservation r ON p.id = r.playing_id "
				+ "JOIN member m ON r.member_id = m.id "
				+ "WHERE p.movie_id=? "
				+ "GROUP BY p.movie_id";
		 try {
		        return jdbcTemplate.queryForObject(sql, new MovieMemberForChartMapper(), id);
		    } catch (EmptyResultDataAccessException e) {
		        MovieMemberForChart defaultResult = new MovieMemberForChart();
		        defaultResult.setMan(0);
		        defaultResult.setWoman(0);
		        defaultResult.setAge10th(0);
		        defaultResult.setAge20th(0);
		        defaultResult.setAge30th(0);
		        defaultResult.setAge40th(0);
		        defaultResult.setAge50th(0);
		        defaultResult.setAge60th(0);
		        defaultResult.setAge70th(0);
		        defaultResult.setAge80th(0);
		        return defaultResult;
		    }
	}

	@Override
	public Boolean isLiked(String memberId, String movieId) {
		String sql = "SELECT COUNT(*) FROM likes "
				+ "WHERE member_id = (SELECT id FROM member WHERE user_id = ?) "
				+ "AND movie_id = ?"
				+ "";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, memberId, movieId);
        return count != null && count > 0;
	}

	@Override
	public void addLike(String memberId, String movieId) {
        String sql = "INSERT INTO likes (id, member_id, movie_id) VALUES (like_seq.nextval, (SELECT id FROM member WHERE user_id = ?), ?)";
        jdbcTemplate.update(sql, memberId, movieId);
    }

    @Override
    public void removeLike(String memberId, String movieId) {
        String sql = "DELETE FROM likes WHERE member_id = (SELECT id FROM member WHERE user_id = ?) AND movie_id = ?";
        jdbcTemplate.update(sql, memberId, movieId);
    }

	@Override
	public List<Movie> getLikedMovies(String memberId) {
		String sql = "select m.id, m.krname, m.enname, m.directors, m.actors, m.release_date, m.open_yn, m.main_image, m.description, m.total_audience, m.like_count, m.nation, m.show_time, m.watch_grade " +
                "FROM likes l " +
                "JOIN movie m ON l.movie_id = m.id " +
                "WHERE l.member_id = (SELECT id FROM member WHERE user_id = ?)";
	   return jdbcTemplate.query(sql, new MovieMapper(), memberId);
	      
	}

	@Override
	public List<Movie> getSearchMovies(String keyword) {
		String sql = "select id, krname, enname, directors, actors, release_date, open_yn, main_image, description, total_audience, like_count, nation, show_time, watch_grade "
				+ "from movie "
				+ "where upper(krname) like upper(?) or upper(enname) like upper(?)";
		String searchPattern = "%" + keyword + "%"; // 검색어 포함을 위한 % 추가
	    return jdbcTemplate.query(sql, new MovieMapper(), searchPattern, searchPattern);
	}

	@Override
	public int getSearchMoviesCouont(String keyword) {
		 String sql = "SELECT COUNT(*) FROM Movie WHERE krName LIKE ? OR enName LIKE ?";
		 String searchPattern = "%" + keyword + "%";
		 return jdbcTemplate.queryForObject(sql, Integer.class, searchPattern, searchPattern);
	}

}
