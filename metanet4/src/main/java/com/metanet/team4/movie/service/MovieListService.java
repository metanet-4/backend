package com.metanet.team4.movie.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final Map<String, Integer> searchMovieCountCache = new HashMap<>();

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

	@Override
	public int getSearchMoviesCount(String keyword) {
		return movieRepository.getSearchMoviesCouont(keyword);
	}
	
	@Scheduled(fixedRate = 10000) 
    public void updateSearchMovieCounts() {
        for (String keyword : searchMovieCountCache.keySet()) {
            int currentCount = getSearchMoviesCount(keyword);
            searchMovieCountCache.put(keyword, currentCount);
        }
    }
	
	public int getCachedSearchMoviesCount(String keyword) {
        return searchMovieCountCache.getOrDefault(keyword, getSearchMoviesCount(keyword));
    }
    
    
}
