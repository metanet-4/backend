package com.metanet.team4.common;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.metanet.team4.jwt.JwtUtil;
import com.metanet.team4.member.mapper.MemberMapper;
import com.metanet.team4.member.model.Member;
import com.metanet.team4.payment.dao.IReservatoinRepository;
import com.metanet.team4.payment.model.PaymentResponseDto;
import com.metanet.team4.payment.model.Reservation;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TestDataUtils {

	private final JwtUtil jwtUtil;
	
	private final MemberMapper memberMapper;
	private final IReservatoinRepository reservationRepository;
	
	public LoginResponseDto getLoginAccessToken() {
        Member member = new Member();
        member.setUserId("testUser1212");
        member.setName("테스트 유저");
        member.setPassword("password123");
        member.setPhone("010-1234-5678");
        member.setEmail("testuser@example.com");
        member.setBirthday(new Date());
        member.setGender(1);
        member.setImage(null);
        member.setDisabilityCertificate(null);         
        member.setIsDiscounted(0);
        member.setRole("USER");
		
		memberMapper.insertMember(member);
		
		Member findMember = memberMapper.findByUserId(member.getUserId());
	    String token = jwtUtil.generateToken(findMember.getUserId(), findMember.getRole());

	    return new LoginResponseDto(token, findMember);
	}
	
    public Long insertReservation(Member member) {
    	
    	Reservation reservation = new Reservation();
    	reservation.setReservationTime(new Date());
    	reservation.setReservationCode(generateUniqueReservationCode());
    	
    	reservation.setPaymentAmount(0);
    	reservation.setReceiptId("test-receipt-id");
    	reservation.setTicketType("일반");
    	reservation.setTicketStatus(1);
    	
    	reservation.setMemberId(member.getId());
    	reservation.setPlayingId(1L);
//    	reservation.setSeatId(1L); // Todo 생성하고 넣는걸로 수정 해야함
    	
    	reservationRepository.insertReservation(reservation);
    	
        return reservation.getId();
    }

    private static Long generateUniqueReservationCode() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
    }
}
