package com.metanet.team4.movie.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.metanet.team4.movie.model.Movie;

@Mapper
public interface MovieListMapper {
	// 박스오피스 영화 목록
	List<Movie> getBoxOfficeMovies();
	
	// 상영예정작 영화 목록
	List<Movie> getComingSoonMovies();
}
