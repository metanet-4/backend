package com.metanet.team4.movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.team4.movie.model.Movie;
import com.metanet.team4.movie.service.IMovieService;

@RestController
@RequestMapping("/movie")
@CrossOrigin(origins = "http://localhost:5173")
public class MovieController {
	
	@Autowired
	IMovieService movieService;

	@GetMapping("/{productId}")
	public Movie MovieDetail(@PathVariable("productId") String productId) {
		Movie movie = movieService.selectMovie(productId);
		System.out.println(movie);
		return movie;
	}
}
