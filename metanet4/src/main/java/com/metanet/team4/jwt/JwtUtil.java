package com.metanet.team4.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.metanet.team4.member.service.RedisService;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final RedisService redisService;
    private static Key SECRET_KEY;

    @Value("${jwt.secret}")
    public void setSecretKey(String secret) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        SECRET_KEY = Keys.hmacShaKeyFor(keyBytes);
        System.out.println("✅ [JWT] SECRET_KEY 로드 완료");
    }

    private static final long ACCESS_EXPIRATION = 1000 * 60 *30; // 30분
    private static final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일

    public String generateToken(String userid, String role) {
        String token = Jwts.builder()
                .setSubject(userid)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
        System.out.println("✅ [JWT 생성] 사용자 ID: " + userid + ", 역할: " + role);
        return token;
    }

    public String generateRefreshToken(String userid) {
        String refreshToken = Jwts.builder()
                .setSubject(userid)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();

        redisService.saveRefreshToken(userid, refreshToken);
        System.out.println("✅ [Refresh Token 저장] 사용자 ID: " + userid);
        return refreshToken;
    }

    public String extractUserid(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            System.out.println("🔴 [extractUserid 오류] " + e.getMessage());
            return null;
        }
    }

    public String extractRole(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("role", String.class);
        } catch (Exception e) {
            System.out.println("🔴 [extractRole 오류] " + e.getMessage());
            return null;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            System.out.println("🔴 [isTokenValid 오류] " + e.getMessage());
            return false;
        }
    }

    public boolean isRefreshTokenValid(String userid, String token) {
        String storedToken = redisService.getRefreshToken(userid);
        return storedToken != null && storedToken.equals(token);
    }

    public void deleteRefreshToken(String userid) {
        redisService.deleteRefreshToken(userid);
    }
}
