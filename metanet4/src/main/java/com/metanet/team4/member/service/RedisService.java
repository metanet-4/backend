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
     * Refresh Token 저장 (유효기간 7일)
     */
    public void saveRefreshToken(String userId, String refreshToken) {
        redisTemplate.opsForValue().set("refreshToken:" + userId, refreshToken, 7, TimeUnit.DAYS);
    }

    /**
     * Refresh Token 조회
     */
    public String getRefreshToken(String userId) {
        return redisTemplate.opsForValue().get("refreshToken:" + userId);
    }

    /**
     * Refresh Token 삭제 (로그아웃 시)
     */
    public void deleteRefreshToken(String userId) {
        redisTemplate.delete("refreshToken:" + userId);
    }
}
