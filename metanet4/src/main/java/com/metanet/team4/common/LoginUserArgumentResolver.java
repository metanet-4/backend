package com.metanet.team4.common;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.metanet.team4.exception.NotFoundException;
import com.metanet.team4.jwt.JwtAuthenticationFilter;
import com.metanet.team4.jwt.JwtUtil;
import com.metanet.team4.member.mapper.MemberMapper;
import com.metanet.team4.member.model.Member;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
	
	private final JwtAuthenticationFilter jwtTokenProvider;
	private final JwtUtil jwtUtil;
	private final MemberMapper memberMapper;
	
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		boolean hasLoginUserAnnotation = parameter.hasParameterAnnotation(Login.class);
		boolean isUserType = Member.class.isAssignableFrom(parameter.getParameterType());
		return hasLoginUserAnnotation && isUserType;
	}
	
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		String token = jwtTokenProvider.getJwtFromCookies((HttpServletRequest) webRequest.getNativeRequest());
		String userId = jwtUtil.extractUserId(token);
		Member member = memberMapper.findByUserId(userId);
		if (member == null) {
		    throw new NotFoundException("해당 userId를 가진 사용자가 존재하지 않습니다.");
		}
		return member;
	}
}