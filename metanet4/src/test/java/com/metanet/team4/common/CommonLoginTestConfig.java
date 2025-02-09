package com.metanet.team4.common;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.metanet.team4.member.model.Member;

@TestConfiguration
public class CommonLoginTestConfig {

 @Bean
 public HandlerMethodArgumentResolver loginArgumentResolver() {
     return new HandlerMethodArgumentResolver() {
         @Override
         public boolean supportsParameter(MethodParameter parameter) {
             return parameter.hasParameterAnnotation(Login.class);
         }
         @Override
         public Object resolveArgument(
             MethodParameter parameter,
             ModelAndViewContainer mavContainer,
             NativeWebRequest webRequest,
             WebDataBinderFactory binderFactory
         ) {
             Member mockMember = new Member();
             mockMember.setId(999L);
             mockMember.setName("MockUser");
             return mockMember;
         }
     };
 }
}
