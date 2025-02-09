package com.metanet.team4.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metanet.team4.jwt.JwtAuthenticationFilter;
import com.metanet.team4.jwt.JwtUtil;
import com.metanet.team4.member.model.Member;
import com.metanet.team4.payment.model.PaymentRequestDto;
import com.metanet.team4.payment.model.PaymentResponseDto;
import com.metanet.team4.payment.service.PaymentService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PaymentController.class)
@Import(PaymentControllerTestConfig.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter; 

    @MockBean 
    private JwtUtil jwtUtil;
    
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        PaymentResponseDto responseDto = new PaymentResponseDto();
        responseDto.setStatus("SUCCESS");

        Mockito.when(paymentService.processPayment(any(PaymentRequestDto.class), any(Member.class)))
               .thenReturn(responseDto);
    }

    @Test
    @DisplayName("결제 요청 API 테스트")
    void requestPaymentTest() throws Exception {
        // Given
        PaymentRequestDto requestDto = new PaymentRequestDto();
        requestDto.setPlayingId(1L);
        requestDto.setReceiptId("receipt-123");
        requestDto.setPaymentAmount(10000);
        requestDto.setTicketType("일반");

        // When & Then
        mockMvc.perform(post("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.message").value("결제 성공"));
    }
}
