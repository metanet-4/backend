package com.metanet.team4.movie.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.team4.common.Login;
import com.metanet.team4.member.model.Member;
import com.metanet.team4.movie.model.Movie;
import com.metanet.team4.movie.model.MovieDetailResponse;
import com.metanet.team4.movie.model.MovieMemberForChart;
import com.metanet.team4.movie.service.IMovieService;
import com.metanet.team4.movie.service.MovieListService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name="Movie", description="movie detail page")
public class MovieController {
	
	@Autowired
	IMovieService movieService;
	
	@Autowired
	MovieListService movieListService;

	@GetMapping("/movie/detail/{movieId}")
	public MovieDetailResponse movieDetail(@PathVariable("movieId") String movieId) {
		Movie movie = movieService.selectMovie(movieId);
		MovieMemberForChart movieMemberForChart = movieService.countForChart(movieId);
	    return new MovieDetailResponse(movie, movieMemberForChart);
	}
	
	@GetMapping("/movie/detail/{movieId}/like")
	public boolean movieLike(@PathVariable("movieId") String movieId, @Login Member member) {
		Boolean isLiked = movieService.isLiked(member.getUserId(), movieId);
	    return isLiked;
	}
	
	@PostMapping("/movie/detail/{movieId}")
    public ResponseEntity<String> toggleLike(@PathVariable String movieId, @Login Member member) {
		boolean isLiked = movieService.isLiked(member.getUserId(), movieId);
        if (isLiked) {
            movieService.removeLike(member.getUserId(), movieId);
            return ResponseEntity.ok("좋아요 취소됨");
        } else {
            movieService.addLike(member.getUserId(), movieId);
            return ResponseEntity.ok("좋아요 추가됨");
        }
    }
	
	@GetMapping("/movie/proxy-image")
	public ResponseEntity<byte[]> proxyImage(@RequestParam String url) throws IOException {
	    URL imageUrl = new URL(url);
	    InputStream in = imageUrl.openStream();
	    
	    // InputStream을 byte[]로 변환
	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	    byte[] temp = new byte[1024];
	    int bytesRead;
	    
	    while ((bytesRead = in.read(temp)) != -1) {
	        buffer.write(temp, 0, bytesRead);
	    }
	    
	    byte[] imageBytes = buffer.toByteArray();

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.IMAGE_JPEG);
	    headers.setContentDisposition(ContentDisposition.attachment().build());

	    return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
	}

	@GetMapping("/likeList")
	public List<Movie> getLikedMovies(@Login Member member) {
		List<Movie> movies = movieService.getLikedMovies(member.getUserId());
		return movies;
	}
	@GetMapping("/movie/boxoffice")
    public List<Movie> getBoxOfficeMovies() {
        return movieListService.getBoxOfficeMovies();
    }

    @GetMapping("/movie/comingsoon")
    public List<Movie> getComingSoonMovies() {
        return movieListService.getComingSoonMovies();
    }
    
    @GetMapping("/movie/search/{keyword}")
    public List<Movie> getSearchMovies(@PathVariable String keyword){
    	return movieListService.getSearchMovies(keyword);
    }
    
    @GetMapping("/movie/search/{keyword}/count")
    public int getSearchMoviesCount(@PathVariable String keyword) {
        return movieListService.getCachedSearchMoviesCount(keyword);
    }
    
}
