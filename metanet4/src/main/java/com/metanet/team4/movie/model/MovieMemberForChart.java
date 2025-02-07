package com.metanet.team4.movie.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieMemberForChart {
	String movieId;
	int man;
	int woman;
	int age10th;
	int age20th;
	int age30th;
	int age40th;
	int age50th;
	int age60th;
	int age70th;
	int age80th;
}
