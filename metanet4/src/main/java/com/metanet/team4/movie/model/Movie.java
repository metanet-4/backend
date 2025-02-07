package com.metanet.team4.movie.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
	String id;
	String krName;
	String enName;
	String directors;
	String actors;
	Date releaseDate;
	String openYn;
	String mainImage;
	String description;
	int totalAudience;
	int likeCount;
	String nation;
	int showTime;
	String watchGrade;
}
