package com.metanet.team4.member.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    

    /**
     * ✅ Refresh Token 저장 (유효기간 7일)
     */
    public void saveRefreshToken(String userId, String refreshToken) {
        redisTemplate.opsForValue().set("refreshToken:" + userId, refreshToken, 7, TimeUnit.DAYS);
        System.out.println("🟢 [Redis] Refresh Token 저장 완료 - 사용자 ID: " + userId);
    }

    /**
     * ✅ Refresh Token 조회
     */
    public String getRefreshToken(String userId) {
        String refreshToken = redisTemplate.opsForValue().get("refreshToken:" + userId);
        System.out.println("🟢 [Redis] Refresh Token 조회 - 사용자 ID: " + userId + ", Token: " + (refreshToken != null ? refreshToken.substring(0, 20) + "..." : "없음"));
        return refreshToken;
    }

    /**
     * ✅ Refresh Token 삭제 (로그아웃 시)
     */
    public void deleteRefreshToken(String userId) {
        redisTemplate.delete("refreshToken:" + userId);
        System.out.println("🟢 [Redis] Refresh Token 삭제 완료 - 사용자 ID: " + userId);
    }
    

    // 인증번호 저장 (5분 후 자동 삭제)
    public void saveAuthCode(String email, String authCode) {
        redisTemplate.opsForValue().set(email, authCode, 5, TimeUnit.MINUTES);
    }

    // 인증번호 조회
    public String getAuthCode(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    // 인증번호 삭제
    public void deleteAuthCode(String email) {
        redisTemplate.delete(email);
    }
}
