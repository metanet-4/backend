package com.metanet.team4.movie.dao;

import org.springframework.stereotype.Repository;

import com.metanet.team4.movie.model.Movie;
import com.metanet.team4.movie.model.MovieMemberForChart;

@Repository
public interface IMovieRepository {
	Movie SelectMovie(String id);
	MovieMemberForChart CountForChart(String id);
	Boolean isLiked(String memberId, String movieId);
	void addLike(String memberId, String movieId);
	void removeLike(String memberId, String movieId);
}
