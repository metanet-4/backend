package com.metanet.team4.mypage.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.metanet.team4.common.LoginResponseDto;
import com.metanet.team4.common.TestDataUtils;
import com.metanet.team4.member.model.Member;

import jakarta.servlet.http.Cookie;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MypageControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	
	@Autowired
	private TestDataUtils testDataUtils;
	
	private String token;
	private Member member;
	private String userid;
	
	@BeforeEach
	void beforeEach() {
		LoginResponseDto loginResponseDto = testDataUtils.getLoginAccessToken();
    	token = loginResponseDto.getToken();
    	member = loginResponseDto.getMember();
	}
	
	@Test
	void getMypageMemberTest() throws Exception {
		mockMvc.perform(get("/mypage")
			.cookie(new Cookie("jwt", token)))
			.andExpect(status().isOk());
	}
}
