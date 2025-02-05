package com.metanet.team4.movie.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.metanet.team4.movie.dao.IMovieRepository;
import com.metanet.team4.movie.model.Movie;

@Service
public class MovieService implements IMovieService {

	@Autowired
	IMovieRepository movieRepository;
	
	@Override
	public Movie selectMovie(String id) {
		return movieRepository.SelectMovie(id);
	}

}
