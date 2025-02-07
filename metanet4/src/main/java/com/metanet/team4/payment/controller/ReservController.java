package com.metanet.team4.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metanet.team4.payment.model.ReservationDetailDto;
import com.metanet.team4.payment.service.ReservService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
public class ReservController {

	private final ReservService reservService;
	
	
	@GetMapping("/{reservationId}")
	public ResponseEntity<ReservationDetailDto> getReservationDetail (@PathVariable Long reservationId) {
		
		ReservationDetailDto response = reservService.getReservationDetail(reservationId);
		return ResponseEntity.ok(response);
	}
}
