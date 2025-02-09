package com.metanet.team4.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.MethodParameter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metanet.team4.common.Login;
import com.metanet.team4.member.model.Member;
import com.metanet.team4.payment.model.PaymentRequestDto;
import com.metanet.team4.payment.model.PaymentResponseDto;
import com.metanet.team4.payment.service.PaymentService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PaymentController.class)
@Import(PaymentControllerTest.TestConfig.class)
class PaymentControllerTest {

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig implements WebMvcConfigurer {
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new HandlerMethodArgumentResolver() {
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
                    // 필요한 필드 추가 세팅
                    return mockMember;
                }
            });
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

        // PaymentResponseDto는 예시
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
