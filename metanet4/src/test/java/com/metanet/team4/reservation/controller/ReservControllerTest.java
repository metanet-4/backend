package com.metanet.team4.reservation.controller;


import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metanet.team4.common.Login;
import com.metanet.team4.member.model.Member;
import com.metanet.team4.payment.controller.ReservController; // 실제 패키지에 맞게 수정
import com.metanet.team4.payment.model.CancelResponseDto;
import com.metanet.team4.payment.model.ReservationDetailDto;
import com.metanet.team4.payment.service.ReservService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ReservController.class)
@Import(ReservControllerTest.TestConfig.class)
class ReservControllerTest {

    @MockBean
    private ReservService reservService;

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
                                              NativeWebRequest webRequest,
                                              WebDataBinderFactory binderFactory) {
                    // 테스트용 Member 목 객체 생성
                    Member mockMember = new Member();
                    mockMember.setId(999L);
                    mockMember.setName("MockUser");
                    // 필요한 필드를 세팅하면 됨
                    return mockMember;
                }
            });
        }
    }

    @Test
    void getReservationDetailTest() throws Exception {
        // given
        ReservationDetailDto detailDto = new ReservationDetailDto();
        detailDto.setCinemaName("테스트영화관");
        detailDto.setScreenName("1관");
        detailDto.setTicketType("일반");

        Mockito.when(reservService.getReservationDetail(1L))
               .thenReturn(detailDto);

        // when & then
        mockMvc.perform(get("/ticket/{reservationId}", 1L))
                .andExpect(status().isOk());
                // .andExpect(jsonPath("$.cinemaName").value("테스트영화관")) 
                // JSON 본문의 필드를 검증하고 싶다면 jsonPath 등 추가
    }

    @Test
    void cancelPaymentTest() throws Exception {
        // given
        CancelResponseDto cancelResponse = new CancelResponseDto("CANCELED", "예매가 취소되었습니다.");
        // cancelReservation(memberId, reservationId)
        // 이 때 memberId = 999L (MockMember)로 동작
        Mockito.when(reservService.cancelReservation(eq(999L), eq(1L)))
               .thenReturn(cancelResponse);

        // when & then
        mockMvc.perform(patch("/ticket/{reservationId}/cancel", 1L))
                .andExpect(status().isOk());
    }
}
