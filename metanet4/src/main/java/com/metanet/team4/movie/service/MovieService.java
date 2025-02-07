package com.metanet.team4.movie.service;

import java.util.List;

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
	public Movie selectMovie(String id) {
		return movieRepository.selectMovie(id);
	}

	@Override
	public MovieMemberForChart countForChart(String id) {
		return movieRepository.countForChart(id);
	}

	@Override
	public Boolean isLiked(String memberId, String movieId) {
		return movieRepository.isLiked(memberId, movieId);
	}

	@Override
	public String addLike(String memberId, String movieId) {
        movieRepository.addLike(memberId, movieId);
        return "좋아요 추가됨";
	}

	@Override
	public String removeLike(String memberId, String movieId) {
		movieRepository.removeLike(memberId, movieId);
		return "좋아요 삭제됨";
	}

	@Override
	public List<Movie> getLikedMovies(String memberId) {
		return movieRepository.getLikedMovies(memberId);
	}

}
