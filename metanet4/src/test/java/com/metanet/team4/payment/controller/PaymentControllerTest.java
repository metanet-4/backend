package com.metanet.team4.payment.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metanet.team4.common.CommonLoginTestConfig;
import com.metanet.team4.common.LoginResponseDto;
import com.metanet.team4.common.TestDataUtils;
import com.metanet.team4.member.model.Member;
import com.metanet.team4.payment.dao.IReservatoinRepository;
import com.metanet.team4.payment.model.PaymentRequestDto;

import jakarta.servlet.http.Cookie;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Transactional
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private TestDataUtils testDataUtils;

    private String token;
    private Member member;
    
    @BeforeEach
    void beforeEach() {
    	LoginResponseDto loginResponseDto = testDataUtils.getLoginAccessToken();
    	token = loginResponseDto.getToken();
    	member = loginResponseDto.getMember();
    }
    
    @Test
    void requestPaymentTest() throws Exception {
        // given
    	    	
        PaymentRequestDto requestDto = new PaymentRequestDto();
        requestDto.setPlayingId(1L);
        requestDto.setReceiptId("receipt123");
        requestDto.setPaymentAmount(10000);
        requestDto.setTicketType("일반");
        requestDto.setSeatNames("E3, E4, E5");

        // when & then
        mockMvc.perform(post("/payment")
                .contentType("application/json")
                .cookie(new Cookie("jwt", token))
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk());
    }
}
