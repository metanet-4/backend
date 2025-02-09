package com.metanet.team4.reservation.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.metanet.team4.jwt.JwtAuthenticationFilter;
import com.metanet.team4.payment.controller.ReservController;
import com.metanet.team4.payment.model.CancelResponseDto;
import com.metanet.team4.payment.model.ReservationDetailDto;
import com.metanet.team4.payment.service.ReservService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ReservController.class)
@Import(ReservControllerTestConfig.class)
class ReservControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservService reservService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter; 
    
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        ReservationDetailDto reservationDetailDto = new ReservationDetailDto();
        reservationDetailDto.setKrName("test 영화");

        Mockito.when(reservService.getReservationDetail(anyLong()))
               .thenReturn(reservationDetailDto);

        CancelResponseDto cancelResponseDto = new CancelResponseDto();
        cancelResponseDto.setStatus("CANCELED");
        cancelResponseDto.setMessage("예매 취소 성공");

        Mockito.when(reservService.cancelReservation(anyLong(), anyLong()))
               .thenReturn(cancelResponseDto);
    }

    @Test
    @DisplayName("예매 상세 조회 API 테스트")
    void getReservationDetailTest() throws Exception {
        mockMvc.perform(get("/ticket/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.krName").value("test 영화"));
    }

    @Test
    @DisplayName("예매 취소 API 테스트")
    void cancelPaymentTest() throws Exception {
        mockMvc.perform(patch("/ticket/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELED"))
                .andExpect(jsonPath("$.message").value("예매 취소 성공"));
    }
}
