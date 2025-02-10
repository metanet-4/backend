package com.metanet.team4.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.metanet.team4.member.service.RedisService;

import javax.crypto.SecretKey;

import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final RedisService redisService;
    private static SecretKey SECRET_KEY;

    @Value("${jwt.secret}")
    public void setSecretKey(String secret) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        SECRET_KEY = Keys.hmacShaKeyFor(keyBytes);
        System.out.println("✅ [JWT] SECRET_KEY 로드 완료");
    }

    private static final long ACCESS_EXPIRATION = 1000 *60*30; // 30분
    private static final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일

    /**
     * ✅ Access Token 생성
     */
    public String generateToken(String userId, String role) {
       if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("🔴 [오류] JWT 생성 시 userId가 null 또는 빈 값입니다.");
        }
       
        return Jwts.builder()
              .claim("userid", userId)  
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * ✅ Refresh Token 생성
     */
    public String generateRefreshToken(String userId,String role) {
        String refreshToken = Jwts.builder()
              .claim("userid", userId)
              .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();

        redisService.saveRefreshToken(userId, refreshToken);
        System.out.println("✅ [Refresh Token 저장] 사용자 ID: " + userId);
        return refreshToken;
    }
    
    /**
     * ✅ 이메일 인증 토큰 생성 (10분 유효) 
     */
    /*public String generateEmailVerificationToken(String userId, String email) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // 10분 유효
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }*/


    /**
     * ✅ JWT에서 사용자 ID 추출
     */
    public String extractUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)  // ✅ setSigningKey()로 서명 검증
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            System.out.println("🟢 [JWT 파싱] claims 내용: " + claims);
            System.out.println("🟢 [JWT 파싱] 추출된 사용자 ID: " + claims.get("userid", String.class));

            return claims.get("userid", String.class);  //"userid" 클레임에서 추출
        } catch (Exception e) {
            System.out.println("🔴 [extractUserId 오류] " + e.getMessage());
            return null;
        }
    }

    /**
     * ✅ JWT에서 역할(Role) 추출
     */
    public String extractRole(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            System.out.println("🟢 [JWT 파싱] claims 내용: " + claims);
            String role = claims.get("role", String.class);

            if (role == null) {
                System.out.println("🔴 [extractRole 오류] role 값이 존재하지 않음 (JWT 생성 시 누락된 가능성)");
            } else {
                System.out.println("🟢 [extractRole] 추출된 역할: " + role);
            }

            return role;
        } catch (Exception e) {
            System.out.println("🔴 [extractRole 오류] " + e.getMessage());
            return null;
        }
    }

    /**
     * ✅ JWT 유효성 검증
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .setAllowedClockSkewSeconds(5)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            System.out.println("🔴 [isTokenValid 오류] " + e.getMessage());
            return false;
        }
    }
    /**
     * ✅ 쿠키에서 Refresh Token 가져오기
     */
    public String getRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }


    /**
     * ✅ Refresh Token 유효성 검증 (Redis 저장된 토큰과 비교)
     */
    public boolean isRefreshTokenValid(String userId, String token) {
        String storedToken = redisService.getRefreshToken(userId);
        return storedToken != null && storedToken.equals(token);
    }

    /**
     * ✅ Refresh Token 삭제
     */
    public void deleteRefreshToken(String userId) {
        redisService.deleteRefreshToken(userId);
    }
}
