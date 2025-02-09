package com.metanet.team4.movie.service;

import java.util.List;

import com.metanet.team4.movie.model.Movie;

public interface IMovieListService {
	List<Movie> getBoxOfficeMovies();
	List<Movie> getComingSoonMovies();
	List<Movie> getSearchMovies(String keyword);
	int getSearchMoviesCount(String keyword);
}
