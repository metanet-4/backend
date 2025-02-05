package com.metanet.team4.member.controller;

import java.util.Map;
import java.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.metanet.team4.jwt.JwtUtil;
import com.metanet.team4.member.dto.LoginRequest;
import com.metanet.team4.member.dto.SignupRequest;
import com.metanet.team4.member.model.Member;
import com.metanet.team4.member.service.MemberService;
import com.metanet.team4.member.service.RedisService;
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
    private final RedisService redisService;

    /**
     * ✅ 회원가입 (추가된 부분)
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@ModelAttribute SignupRequest request) {
        try {
            memberService.registerUser(request);
            return ResponseEntity.ok("회원가입 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    /**
     * ✅ 로그인 (Access Token + Refresh Token 발급)
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        System.out.println("🔹 로그인 요청: " + request.getUserid());

        Member member = memberService.findByUserid(request.getUserid());
        if (member == null) {
            return ResponseEntity.status(401).body(Map.of("error", "아이디 또는 비밀번호가 잘못되었습니다."));
        }

        // ✅ 최신 역할 가져오기
        String role = member.getRole();
        if (role == null || role.isEmpty()) role = "ROLE_USER";

        // ✅ JWT 토큰 생성
        String accessToken = jwtUtil.generateToken(member.getUserId(), role);
        String refreshToken = jwtUtil.generateRefreshToken(member.getUserId());

        // ✅ Access Token을 HttpOnly 쿠키에 저장
        Cookie accessTokenCookie = new Cookie("jwt", accessToken);
        accessTokenCookie.setHttpOnly(true);  // 보안 강화
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(30 * 60);
        response.addCookie(accessTokenCookie);

        // ✅ Refresh Token을 Redis에 저장
        redisService.saveRefreshToken(member.getUserId(), refreshToken);

        System.out.println("🟢 [로그인 성공] Access Token은 HttpOnly 쿠키에 저장, Refresh Token은 Redis에 저장됨");

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
     * ✅ Access Token 재발급 (Refresh Token을 Redis에서 확인 후 발급)
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String userid = getUserIdFromCookies(request);

        if (userid == null) {
            return ResponseEntity.status(401).body(Map.of("error", "인증되지 않은 사용자"));
        }

        String refreshToken = redisService.getRefreshToken(userid);
        if (refreshToken == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Refresh Token이 존재하지 않음"));
        }

        // ✅ 최신 역할 가져오기 (DB에서)
        Member member = memberService.findByUserid(userid);
        if (member == null) {
            return ResponseEntity.status(401).body(Map.of("error", "사용자를 찾을 수 없음"));
        }
        String role = member.getRole();

        // ✅ 새로운 Access Token 발급
        String newAccessToken = jwtUtil.generateToken(userid, role);

        // ✅ Access Token을 HttpOnly 쿠키에 저장
        Cookie accessTokenCookie = new Cookie("jwt", newAccessToken);
        accessTokenCookie.setHttpOnly(true); // 보안 강화
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(30 * 60);
        response.addCookie(accessTokenCookie);

        System.out.println("🟢 [Access Token 재발급 완료] 사용자 ID: " + userid);

        return ResponseEntity.ok(Map.of("message", "Access Token 재발급 완료"));
    }

    /**
     * ✅ 로그아웃 (Redis에서 Refresh Token 삭제 + 쿠키 삭제)
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String userid = getUserIdFromCookies(request);
        if (userid != null) {
            redisService.deleteRefreshToken(userid);
            System.out.println("🟢 [로그아웃] Redis에서 Refresh Token 삭제 - 사용자 ID: " + userid);
        }

        // ✅ 쿠키에서 Access Token 삭제
        Cookie accessTokenCookie = new Cookie("jwt", null);
        accessTokenCookie.setHttpOnly(true); // 보안 강화
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);
        response.addCookie(accessTokenCookie);

        System.out.println("🟢 [로그아웃] 성공 - Access Token 쿠키 삭제 완료");

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
     * ✅ 쿠키에서 사용자 ID 가져오기
     */
    private String getUserIdFromCookies(HttpServletRequest request) {
        String token = getJwtFromCookies(request);
        return token != null ? jwtUtil.extractUserid(token) : null;
    }
}
