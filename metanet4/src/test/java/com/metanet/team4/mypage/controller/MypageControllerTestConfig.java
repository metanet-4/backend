package com.metanet.team4.mypage.controller;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.metanet.team4.mypage.service.MypageService;

@Configuration
public class MypageControllerTestConfig {
	
	@Bean
    public MypageService mypageService() {
        return Mockito.mock(MypageService.class);
    }
}
