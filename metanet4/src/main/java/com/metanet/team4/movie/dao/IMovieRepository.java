package com.metanet.team4.movie.dao;

import org.springframework.stereotype.Repository;

import com.metanet.team4.movie.model.Movie;

@Repository
public interface IMovieRepository {
	Movie SelectMovie(String id);
}
