package com.metanet.team4.payment.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metanet.team4.exception.CustomException;
import com.metanet.team4.member.model.Member;
import com.metanet.team4.payment.dao.IReservatoinRepository;
import com.metanet.team4.payment.model.PaymentRequestDto;
import com.metanet.team4.payment.model.PaymentResponseDto;
import com.metanet.team4.payment.model.Reservation;
import com.metanet.team4.ticket.dao.ISeatRepository;
import com.metanet.team4.ticket.model.Seat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

	@Value("${bootpay.api.application-id}")
	private String bootpayApplicationId;

	@Value("${bootpay.api.private-key}")
	private String bootpayPrivateKey;

	private final IReservatoinRepository reservationRepository;
	private final ISeatRepository seatRepository;

	private final RedissonClient redissonClient;

	@Autowired
    public PaymentService(RedissonClient redissonClient,
                                  ISeatRepository seatRepository,
                                  IReservatoinRepository reservationRepository) {
        this.redissonClient = redissonClient;
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
    }
    @Transactional
    public PaymentResponseDto processPayment(PaymentRequestDto request, Member member) {

        // 입력 검증
        if (request == null || request.getPaymentAmount() == null || request.getReceiptId() == null
                || request.getTicketType() == null || request.getPlayingId() == null) {
            log.warn("결제 요청 데이터 검증 실패: {}", request);
            throw new CustomException("결제 요청 데이터가 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // 예약 정보 생성
        Reservation reservation = new Reservation();
        reservation.setReservationTime(new Date());
        reservation.setReservationCode(generateUniqueReservationCode());
        reservation.setPaymentAmount(request.getPaymentAmount());
        reservation.setReceiptId(request.getReceiptId());
        reservation.setTicketType(request.getTicketType());
        reservation.setTicketStatus(1);
        reservation.setMemberId(member.getId());
        reservation.setPlayingId(request.getPlayingId());

        reservationRepository.insertReservation(reservation);
        if (reservation.getId() == null) {
            log.error("예약 저장 실패: {}", reservation);
            throw new CustomException("예약 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("예매 정보 저장 완료: {}", reservation);

        // 좌석 데이터 생성 및 분산 락을 이용하여 저장
        List<Seat> seats = parseSeatNames(request.getSeatNames(), request.getPlayingId(), reservation.getId());
        if (!seats.isEmpty()) {
            for (Seat seat : seats) {
                // 좌석별로 고유한 락 키 생성: "lock:seat:{playingId}:{seatName}"
                String lockKey = "lock:seat:" + seat.getPlayingId() + ":" + seat.getName();
                RLock lock = redissonClient.getLock(lockKey);
                try {
                    // 락 획득: 최대 10초 기다리고, 획득하면 10초 동안 유지 (필요에 따라 시간 조정)
                    if (!lock.tryLock(10, 180, TimeUnit.SECONDS)) {
                        throw new CustomException("해당 좌석(" + seat.getName() + ")에 대한 락을 획득하지 못했습니다.", HttpStatus.CONFLICT);
                    }
                    // 좌석 INSERT
                    seatRepository.insertSeat(seat);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new CustomException("락 획득 도중 인터럽트 발생", HttpStatus.INTERNAL_SERVER_ERROR);
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            }
            log.info("좌석 정보 저장 완료: {}", seats);
        } else {
            log.warn("등록된 좌석이 없음: seatNames={}", request.getSeatNames());
        }

        // 응답 생성
        PaymentResponseDto response = new PaymentResponseDto();
        response.setReceiptId(request.getReceiptId());
        response.setStatus("SUCCESS");
        response.setPaidAmount(reservation.getPaymentAmount());
        response.setReservationId(reservation.getId());
        response.setReservationCode(reservation.getReservationCode());

        String movieName = reservationRepository.getMovieName(request.getPlayingId());
        response.setMovidName(movieName);

        return response;
    }

	private static Long generateUniqueReservationCode() {
		Random random = new Random();
		return 10000000L + random.nextInt(90000000); // 10000000 ~ 99999999 범위
	}

	private List<Seat> parseSeatNames(String seatNames, Long playingId, Long reservationId) {
		if (seatNames == null || seatNames.trim().isEmpty()) {
			return List.of();
		}

		return Arrays.stream(seatNames.split(",")).map(String::trim).map(seatName -> {
			Seat seat = new Seat();
			seat.setName(seatName);
			seat.setPlayingId(playingId);
			seat.setReservationId(reservationId);
			return seat;
		}).collect(Collectors.toList());
	}
}
