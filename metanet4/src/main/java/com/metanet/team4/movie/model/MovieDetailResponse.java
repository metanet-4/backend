package com.metanet.team4.movie.model;

public class MovieDetailResponse {
    private Movie movie;
    private MovieMemberForChart movieMemberForChart;

    public MovieDetailResponse(Movie movie, MovieMemberForChart movieMemberForChart) {
        this.movie = movie;
        this.movieMemberForChart = movieMemberForChart;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public MovieMemberForChart getMovieMemberForChart() {
        return movieMemberForChart;
    }

    public void setMovieMemberForChart(MovieMemberForChart movieMemberForChart) {
        this.movieMemberForChart = movieMemberForChart;
    }
}
