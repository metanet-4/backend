package com.metanet.team4.movie.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.metanet.team4.movie.dao.MovieRepository;
import com.metanet.team4.movie.mapper.MovieListMapper;
import com.metanet.team4.movie.model.Movie;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovieListService implements IMovieListService{
    private final MovieListMapper movieListMapper;
    
    @Autowired
    private MovieRepository movieRepository;

    // 박스오피스 목록 반환
    @Override
    public List<Movie> getBoxOfficeMovies() {
        return movieListMapper.getBoxOfficeMovies();
    }

    // 상영예정작 목록 반환
    @Override
    public List<Movie> getComingSoonMovies() {
        return movieListMapper.getComingSoonMovies();
    }

	@Override
	public List<Movie> getSearchMovies(String keyword) {
		return movieRepository.getSearchMovies(keyword);
	}
    
    
}
