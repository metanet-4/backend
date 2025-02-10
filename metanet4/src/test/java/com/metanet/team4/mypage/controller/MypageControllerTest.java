package com.metanet.team4.mypage.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metanet.team4.common.CommonLoginTestConfig;
import com.metanet.team4.jwt.JwtAuthenticationFilter;
import com.metanet.team4.mypage.dao.IMypageRepository;
import com.metanet.team4.mypage.model.MypageMember;

@SpringBootTest
@AutoConfigureMockMvc
@Import(CommonLoginTestConfig.class)
public class MypageControllerTest {

	@MockBean
	private IMypageRepository mypageRepository;
	
	@MockBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Test
	void getMypageMemberTest() throws Exception {
		MypageMember mypageMember = new MypageMember();
		mypageMember.setName("테스트회원");
		mypageMember.setGender(1);
		
		Mockito.when(mypageRepository.getMypageMember("aaa"));
	}
}
