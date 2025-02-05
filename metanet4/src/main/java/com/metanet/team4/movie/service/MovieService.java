package com.metanet.team4.movie.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.metanet.team4.movie.dao.IMovieRepository;
import com.metanet.team4.movie.model.Movie;
import com.metanet.team4.movie.model.MovieMemberForChart;

@Service
public class MovieService implements IMovieService {

	@Autowired
	IMovieRepository movieRepository;
	
	@Override
	public Movie SelectMovie(String id) {
		return movieRepository.SelectMovie(id);
	}

	@Override
	public MovieMemberForChart CountForChart(String id) {
		return movieRepository.CountForChart(id);
	}

}
