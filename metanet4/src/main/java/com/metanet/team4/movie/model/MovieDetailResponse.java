package com.metanet.team4.movie.model;

import lombok.Data;

@Data
public class MovieDetailResponse {
    private Movie movie;
    private MovieMemberForChart movieMemberForChart;
    private Boolean isLiked;
    public MovieDetailResponse(Movie movie, MovieMemberForChart movieMemberForChart, Boolean isLiked) {
        this.movie = movie;
        this.movieMemberForChart = movieMemberForChart;
        this.isLiked = isLiked;
    }
}
