package com.metanet.team4.reservation.controller;

import com.metanet.team4.payment.service.ReservService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReservControllerTestConfig {
    
    @Bean
    public ReservService reservService() {
        return Mockito.mock(ReservService.class);
    }
}