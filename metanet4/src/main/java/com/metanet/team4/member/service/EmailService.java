package com.metanet.team4.member.service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    private static final long EXPIRATION_TIME = 5 * 60; // 5분 (초 단위)

    /**
     * 이메일로 인증번호 전송
     */
    public void sendVerificationEmail(String email) {
        String verificationCode = generateVerificationCode();

        try {
            // 이메일 전송
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("회원가입 인증번호");
            message.setText("인증번호: " + verificationCode);
            mailSender.send(message);

            // Redis에 저장 (5분 후 만료)
            redisTemplate.opsForValue().set(email, verificationCode, EXPIRATION_TIME, TimeUnit.SECONDS);

            System.out.println("✅ 이메일 전송 성공: " + email);
        } catch (Exception e) {
            System.err.println("❌ 이메일 전송 실패: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("이메일 전송 실패: " + e.getMessage());
        }
    }

    /**
     * 이메일 인증번호 검증
     */
    public boolean verifyEmail(String email, String inputCode) {
        String storedCode = redisTemplate.opsForValue().get(email);
        if (storedCode != null && storedCode.equals(inputCode)) {
            redisTemplate.delete(email);
            return true;
        }
        return false;
    }

    /**
     * 이메일 인증 여부 확인 (회원가입 시 체크)
     */
    public boolean isEmailVerified(String email) {
        return redisTemplate.opsForValue().get(email) == null;
    }

    /**
     * 6자리 랜덤 인증번호 생성
     */
    private String generateVerificationCode() {
        return String.format("%06d", new SecureRandom().nextInt(1000000));
    }
}
