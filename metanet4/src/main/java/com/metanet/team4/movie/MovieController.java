package com.metanet.team4.movie;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.team4.movie.model.Movie;
import com.metanet.team4.movie.model.MovieDetailResponse;
import com.metanet.team4.movie.model.MovieMemberForChart;
import com.metanet.team4.movie.service.IMovieService;
import com.metanet.team4.movie.service.MovieListService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/movie")
@Tag(name="Movie", description="movie detail page")
public class MovieController {
	
	@Autowired
	IMovieService movieService;
	
	@Autowired
	MovieListService movieListService;

	@GetMapping("/{movieId}")
	public MovieDetailResponse MovieDetail(@PathVariable("movieId") String movieId) {
		Movie movie = movieService.selectMovie(movieId);
		MovieMemberForChart movieMemberForChart = movieService.countForChart(movieId);
		Boolean isLiked = movieService.isLiked("aaa", movieId);
	    return new MovieDetailResponse(movie, movieMemberForChart, isLiked);
	}
	
	@PostMapping("/{movieId}")
    public ResponseEntity<String> toggleLike(@PathVariable String movieId) {
        String memberId="aaa";
		boolean isLiked = movieService.isLiked(memberId, movieId);
        if (isLiked) {
            movieService.removeLike(memberId, movieId);
            return ResponseEntity.ok("좋아요 취소됨");
        } else {
            movieService.addLike(memberId, movieId);
            return ResponseEntity.ok("좋아요 추가됨");
        }
    }
	

	@GetMapping("/likeList")
	public List<Movie> getLikedMovies() {
		String memberId="aaa";
		List<Movie> movies = movieService.getLikedMovies(memberId);
		return movies;
	}
	@GetMapping("/boxoffice")
    public List<Movie> getBoxOfficeMovies() {
        return movieListService.getBoxOfficeMovies();
    }

    @GetMapping("/comingsoon")
    public List<Movie> getComingSoonMovies() {
        return movieListService.getComingSoonMovies();
    }
}
