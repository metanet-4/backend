package com.metanet.team4.reservation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import com.metanet.team4.payment.model.Reservation;
import com.metanet.team4.payment.model.ReservationDetailDto;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Import(CommonLoginTestConfig.class)
class ReservControllerTest {

    // Service는 실제 Bean 사용
    // 대신 Repository만 Mock
    @MockBean
    private IReservatoinRepository repository;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getReservationDetailTest() throws Exception {
        // given
        ReservationDetailDto detailDto = new ReservationDetailDto();
        detailDto.setCinemaName("테스트영화관");
        detailDto.setScreenName("1관");
        detailDto.setTicketType("일반");

        Mockito.when(repository.getReservationDetail(1L))
               .thenReturn(detailDto);

        // when & then
        mockMvc.perform(get("/ticket/{reservationId}", 1L))
               .andExpect(status().isOk());
    }

    @Test
    void cancelPaymentTest() throws Exception {
        // given
        
        // 1) 예약 조회 Mock
        Reservation mockReservation = new Reservation();
        mockReservation.setId(1L);
        mockReservation.setMemberId(999L);
        Mockito.when(repository.getReervationByUserIdAndId(999L, 1L))
               .thenReturn(mockReservation);

        // 2) 티켓 상태 업데이트 Mock
        Mockito.when(repository.updateTicketStatus(1L, 0))
               .thenReturn(1); // 성공적으로 1건 수정

        // when & then
        mockMvc.perform(patch("/ticket/{reservationId}/cancel", 1L))
               .andExpect(status().isOk());
    }
}
