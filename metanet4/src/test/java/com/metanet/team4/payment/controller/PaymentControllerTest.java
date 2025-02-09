package com.metanet.team4.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.MethodParameter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metanet.team4.common.Login;
import com.metanet.team4.member.model.Member;
import com.metanet.team4.payment.model.PaymentRequestDto;
import com.metanet.team4.payment.model.PaymentResponseDto;
import com.metanet.team4.payment.service.PaymentService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Import(PaymentControllerTest.TestConfig.class)  // 커스텀 ArgumentResolver 등록을 원한다면 유지
class PaymentControllerTest {

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {

        /** 
         * MockMvc 테스트 시, @Login 파라미터를 강제 주입하기 위한 커스텀 ArgumentResolver 예시
         * 원본 코드를 유지하려면 아래처럼 Bean 등록하거나,
         * 또는 WebMvcConfigurer를 구현해서 addArgumentResolvers() 등록하는 식으로 바꿀 수도 있음
         */
        @Bean
        public HandlerMethodArgumentResolver loginArgumentResolver() {
            return new HandlerMethodArgumentResolver() {
                @Override
                public boolean supportsParameter(MethodParameter parameter) {
                    return parameter.hasParameterAnnotation(Login.class);
                }
                @Override
                public Object resolveArgument(MethodParameter parameter,
                                              ModelAndViewContainer mavContainer,
                                              org.springframework.web.context.request.NativeWebRequest webRequest,
                                              WebDataBinderFactory binderFactory) {
                    // 테스트용 Member 목 객체 생성
                    Member mockMember = new Member();
                    mockMember.setId(999L);
                    mockMember.setName("MockUser");
                    // 필요하면 다른 필드도 세팅
                    return mockMember;
                }
            };
        }
    }

    @Test
    void requestPaymentTest() throws Exception {
        // given
        PaymentRequestDto requestDto = new PaymentRequestDto();
        requestDto.setPlayingId(123L);
        requestDto.setReceiptId("receipt123");
        requestDto.setPaymentAmount(10000);
        requestDto.setTicketType("일반");

        PaymentResponseDto responseDto = new PaymentResponseDto();
        responseDto.setStatus("SUCCESS");
        responseDto.setReceiptId("receipt123");

        Mockito.when(paymentService.processPayment(any(PaymentRequestDto.class), any(Member.class)))
               .thenReturn(responseDto);

        // when & then
        mockMvc.perform(post("/payment")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }
}
