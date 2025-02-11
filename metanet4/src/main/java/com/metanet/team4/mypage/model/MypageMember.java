package com.metanet.team4.mypage.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class MypageMember {
	private int id;
	private String userId;
	private String name;
	private String email;
	private Date birthday;
	private byte[] image;
	private int gender;
}
