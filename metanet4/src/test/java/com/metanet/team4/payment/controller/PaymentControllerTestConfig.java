package com.metanet.team4.payment.controller;

import com.metanet.team4.payment.service.PaymentService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentControllerTestConfig {
    
    @Bean
    public PaymentService paymentService() {
        return Mockito.mock(PaymentService.class);
    }
}
