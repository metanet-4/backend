package com.metanet.team4.reservation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metanet.team4.common.LoginResponseDto;
import com.metanet.team4.common.TestDataUtils;
import com.metanet.team4.member.model.Member;

import jakarta.servlet.http.Cookie;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Transactional
class ReservControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private TestDataUtils testDataUtils;
    
    private String token;
    private Member member;
    private Long reservationId;
    
    @BeforeEach
    void beforeEach() {
    	LoginResponseDto loginResponseDto = testDataUtils.getLoginAccessToken();
    	token = loginResponseDto.getToken();
    	member = loginResponseDto.getMember();
    	reservationId = testDataUtils.insertReservation(member);
    }

    @Test
    void getReservationDetailTest() throws Exception {
        // given

        // when & then
        mockMvc.perform(get("/ticket/{reservationId}", reservationId)
        		.cookie(new Cookie("jwt", token)))
               .andExpect(status().isOk());
    }

    @Test
    void cancelPaymentTest() throws Exception {
        // given

        // when & then
        mockMvc.perform(patch("/ticket/{reservationId}/cancel", reservationId)
        		.cookie(new Cookie("jwt", token)))
               .andExpect(status().isOk());
    }
}
