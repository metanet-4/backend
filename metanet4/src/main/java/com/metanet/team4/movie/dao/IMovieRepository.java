package com.metanet.team4.movie.dao;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.metanet.team4.movie.model.Movie;
import com.metanet.team4.movie.model.MovieMemberForChart;

@Repository
public interface IMovieRepository {
	Movie selectMovie(String id);
	MovieMemberForChart countForChart(String id);
	Boolean isLiked(String memberId, String movieId);
	void addLike(String memberId, String movieId);
	void removeLike(String memberId, String movieId);
	List<Movie> getLikedMovies(String memberId);

}
