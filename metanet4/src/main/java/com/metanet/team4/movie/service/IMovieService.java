package com.metanet.team4.movie.service;

import java.util.List;

import com.metanet.team4.movie.model.Movie;
import com.metanet.team4.movie.model.MovieMemberForChart;

public interface IMovieService {
	Movie selectMovie(String id);
	MovieMemberForChart countForChart(String id);
	Boolean isLiked(String memberId, String movieId);
	String addLike(String memberId, String movieId);
	String removeLike(String memberId, String movieId);
	List<Movie> getLikedMovies(String memberId);
}
