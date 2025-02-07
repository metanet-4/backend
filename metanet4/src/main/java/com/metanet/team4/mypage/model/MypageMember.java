package com.metanet.team4.mypage.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MypageMember {
	String userId;
	String name;
	String email;
	Date birthday;
	byte[] image;
	int gender;
}
