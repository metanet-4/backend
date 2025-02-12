package com.metanet.team4.movie.model;

import lombok.Data;

@Data
public class MovieDetailResponse {
    private Movie movie;
    private MovieMemberForChart movieMemberForChart;
    public MovieDetailResponse(Movie movie, MovieMemberForChart movieMemberForChart) {
        this.movie = movie;
        this.movieMemberForChart = movieMemberForChart;
    }
}
