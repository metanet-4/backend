package com.metanet.team4.movie.service;

import com.metanet.team4.movie.model.Movie;
import com.metanet.team4.movie.model.MovieMemberForChart;

public interface IMovieService {
	Movie SelectMovie(String id);
	MovieMemberForChart CountForChart(String id);
}
