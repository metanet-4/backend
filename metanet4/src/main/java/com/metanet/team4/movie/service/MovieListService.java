package com.metanet.team4.movie.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.metanet.team4.movie.mapper.MovieListMapper;
import com.metanet.team4.movie.model.Movie;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovieListService {
    private final MovieListMapper movieListMapper;

    // 박스오피스 목록 반환
    public List<Movie> getBoxOfficeMovies() {
        return movieListMapper.getBoxOfficeMovies();
    }

    // 상영예정작 목록 반환
    public List<Movie> getComingSoonMovies() {
        return movieListMapper.getComingSoonMovies();
    }
}
