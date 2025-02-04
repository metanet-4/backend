package com.metanet.team4.member.controller;

import java.util.Map;
import java.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.metanet.team4.jwt.JwtUtil;
import com.metanet.team4.member.dto.LoginRequest;
import com.metanet.team4.member.model.Member;
import com.metanet.team4.member.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    /**
     * ✅ 로그인 (Access Token + Refresh Token 발급 및 쿠키 저장)
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        System.out.println("🔹 로그인 요청: " + request.getUserid());

        Member member = memberService.findByUserid(request.getUserid());
        if (member == null) {
            System.out.println("🔴 [오류] 사용자를 찾을 수 없음: " + request.getUserid());
            return ResponseEntity.status(401).body(Map.of("error", "아이디 또는 비밀번호가 잘못되었습니다."));
        }

        // ✅ 역할 기본값 설정 (null 방지)
        String role = (member.getRole() == null || member.getRole().isEmpty()) ? "ROLE_USER" : member.getRole();
        System.out.println("🟢 [로그인 성공] 사용자 ID: " + member.getUserid() + ", 역할: " + role);

        // ✅ JWT 토큰 생성
        String accessToken = jwtUtil.generateToken(member.getUserid(), role);
        String refreshToken = jwtUtil.generateRefreshToken(member.getUserid());

        // ✅ Access Token을 쿠키에 저장 (HttpOnly X - JS에서 접근 가능)
        setCookie(response, "jwt", accessToken, false, 30 * 60);

        // ✅ Refresh Token을 HttpOnly 쿠키에 저장
        setCookie(response, "refreshToken", refreshToken, true, 7 * 24 * 60 * 60);

        System.out.println("🟢 [로그인 성공] Access Token 및 Refresh Token이 쿠키에 저장됨");
        return ResponseEntity.ok(Map.of("message", "로그인 성공"));
    }

    /**
     * ✅ 로그인 상태 확인 API (쿠키에서 JWT 확인)
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkLoginStatus(HttpServletRequest request) {
        String token = getJwtFromCookies(request);
        if (token != null && jwtUtil.isTokenValid(token)) {
            String role = jwtUtil.extractRole(token);
            return ResponseEntity.ok(Map.of("loggedIn", true, "role", role));
        }
        return ResponseEntity.ok(Map.of("loggedIn", false));
    }

    /**
     * ✅ Access Token 재발급 (Refresh Token 사용)
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookies(request);
        if (refreshToken == null || !jwtUtil.isTokenValid(refreshToken)) {
            System.out.println("🔴 [Access Token 재발급 실패] Refresh Token 없음 또는 유효하지 않음");
            return ResponseEntity.status(401).body(Map.of("error", "Refresh Token이 유효하지 않음"));
        }

        String userid = jwtUtil.extractUserid(refreshToken);
        if (!jwtUtil.isRefreshTokenValid(userid, refreshToken)) {
            System.out.println("🔴 [Access Token 재발급 실패] Redis에서 Refresh Token이 유효하지 않음!");
            return ResponseEntity.status(401).body(Map.of("error", "Refresh Token이 유효하지 않음"));
        }

        String role = jwtUtil.extractRole(refreshToken);
        String newAccessToken = jwtUtil.generateToken(userid, role);

        // ✅ 새 Access Token을 쿠키에 저장
        setCookie(response, "jwt", newAccessToken, false, 30 * 60);

        System.out.println("🟢 [Access Token 재발급] 성공 - 사용자 ID: " + userid);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    /**
     * ✅ 로그아웃 (Redis에서 Refresh Token 삭제 + 쿠키 삭제)
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookies(request);
        if (refreshToken != null) {
            String userid = jwtUtil.extractUserid(refreshToken);
            if (userid != null) {
                System.out.println("🟢 [로그아웃] Redis에서 Refresh Token 삭제 - 사용자 ID: " + userid);
                jwtUtil.deleteRefreshToken(userid);
            }
        }

        // ✅ 쿠키에서 Access Token & Refresh Token 삭제
        removeCookie(response, "jwt");
        removeCookie(response, "refreshToken");

        System.out.println("🟢 [로그아웃] 성공 - 쿠키 삭제 완료");
        return ResponseEntity.ok("로그아웃 성공");
    }

    /**
     * ✅ JWT 쿠키에서 Access Token 가져오기
     */
    private String getJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "jwt".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * ✅ 쿠키에서 Refresh Token 가져오기
     */
    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * ✅ 쿠키 설정 함수
     */
    private void setCookie(HttpServletResponse response, String name, String value, boolean httpOnly, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * ✅ 쿠키 삭제 함수
     */
    private void removeCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
