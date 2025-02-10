package com.metanet.team4.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metanet.team4.common.CommonLoginTestConfig;
import com.metanet.team4.jwt.JwtAuthenticationFilter;
import com.metanet.team4.payment.dao.IReservatoinRepository;
import com.metanet.team4.payment.model.PaymentRequestDto;
import com.metanet.team4.payment.model.Reservation;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Import(CommonLoginTestConfig.class)
class PaymentControllerTest {

    @MockBean
    private IReservatoinRepository reservationRepository; 
    // ↑ Service가 주입받는 Repository를 Mock 처리

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void requestPaymentTest() throws Exception {
        // given
        PaymentRequestDto requestDto = new PaymentRequestDto();
        requestDto.setPlayingId(123L);
        requestDto.setReceiptId("receipt123");
        requestDto.setPaymentAmount(10000);
        requestDto.setTicketType("일반");

        // repository Mock 동작 지정: insertReservation(...)이 호출되면, 
        // 인자로 들어온 reservation에 ID를 세팅해주는 식으로 가정 (DB Auto-Increment를 흉내)
        Mockito.doAnswer(invocation -> {
            // 첫 번째 파라미터(Reservation)를 꺼냄
            var reservationArg = invocation.getArgument(0, Reservation.class);
             reservationArg.setId(999L);
            return null;
        }).when(reservationRepository).insertReservation(any());

        // when & then
        mockMvc.perform(post("/payment")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk());
    }
}
