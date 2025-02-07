package com.metanet.team4.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.metanet.team4.common.LoginUserArgumentResolver;
import com.metanet.team4.jwt.JwtAuthenticationFilter;
import com.metanet.team4.jwt.JwtUtil;
import com.metanet.team4.member.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AuthWebConfig implements WebMvcConfigurer {
	
	private final JwtAuthenticationFilter jwtTokenProvider;
	private final JwtUtil jwtUtil;
	private final MemberMapper memberMapper;
	
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new LoginUserArgumentResolver(jwtTokenProvider, jwtUtil, memberMapper));
	}
}