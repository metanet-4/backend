package com.metanet.team4.member.controller;

import java.util.Map;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.metanet.team4.jwt.JwtUtil;
import com.metanet.team4.member.dto.LoginRequest;
import com.metanet.team4.member.dto.SignupRequest;
import com.metanet.team4.member.model.Member;
import com.metanet.team4.member.service.EmailService;
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
    private final EmailService emailService;

    /**
     * ✅ 회원가입
     */
    /**
     * ✅ 아이디 중복 확인 API
     */
    @GetMapping("/check-userId")
    public ResponseEntity<Boolean> checkUserId(@RequestParam String userId) {
        boolean isDuplicate = memberService.isUserIdDuplicate(userId);
        return ResponseEntity.ok(isDuplicate);
    }

    /**
     * ✅ 전화번호 중복 확인 API
     */
    @GetMapping("/check-phone")
    public ResponseEntity<Boolean> checkPhone(@RequestParam String phone) {
        boolean isDuplicate = memberService.isPhoneDuplicate(phone);
        return ResponseEntity.ok(isDuplicate);
    }
    
    @PostMapping("/send-code")
    public ResponseEntity<String> sendAuthCode(@RequestParam String email) {
        String authCode = emailService.generateAuthCode(); // 인증번호 생성
        emailService.sendAuthCode(email, authCode); // 이메일 전송
        redisService.saveAuthCode(email, authCode); // Redis에 저장

        return ResponseEntity.ok("인증번호가 이메일로 전송되었습니다.");
    }
    
    @PostMapping("/verify-code")
    public ResponseEntity<Boolean> verifyAuthCode(@RequestParam String email, @RequestParam String authCode) {
        String storedCode = redisService.getAuthCode(email);
        
        if (storedCode != null && storedCode.equals(authCode)) {
            redisService.deleteAuthCode(email); // 인증번호 사용 후 삭제
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }



    /**
     * ✅ 회원가입 API
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(
            @RequestParam String userId,
            @RequestParam String name,
            @RequestParam String password,
            @RequestParam String password2,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthday, // ✅ Date 타입으로 변경
            @RequestParam Integer gender, // ✅ Integer 타입으로 변경
            @RequestParam(required = false) MultipartFile disabilityCertificate) { // ✅ 파일 필드는 MultipartFile

        Map<String, String> response = new HashMap<>();
        try {
            // ✅ DTO 객체 생성
            SignupRequest request = new SignupRequest();
            request.setUserId(userId);
            request.setName(name);
            request.setPassword(password);
            request.setPassword2(password2);
            request.setPhone(phone);
            request.setEmail(email);
            request.setBirthday(birthday);
            request.setGender(gender);
            request.setDisabilityCertificate(disabilityCertificate);

            memberService.registerUser(request);
            response.put("message", "회원가입이 완료되었습니다.");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }




    /**
     * ✅ 로그인 (Access Token + Refresh Token 발급)
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        System.out.println("🔹 로그인 요청: " + request.getUserId()); 

        if (request.getUserId() == null || request.getUserId().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "userId가 비어 있습니다."));
        }

        Member member = memberService.findByUserId(request.getUserId());
        if (member == null) {
            return ResponseEntity.status(401).body(Map.of("error", "아이디 또는 비밀번호가 잘못되었습니다."));
        }

        String role = member.getRole();
        if (role == null || role.isEmpty()) role = "ROLE_USER";

        String accessToken = jwtUtil.generateToken(member.getUserId(), role);
        String refreshToken = jwtUtil.generateRefreshToken(member.getUserId(),role);

        // ✅ Access Token을 HttpOnly 쿠키에 저장
        Cookie accessTokenCookie = new Cookie("jwt", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(30 * 60);
        response.addCookie(accessTokenCookie);

        // ✅ Refresh Token도 쿠키에 저장
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);  // 7일
        response.addCookie(refreshTokenCookie);

        // ✅ Refresh Token을 Redis에도 저장 (보안을 위해)
        redisService.saveRefreshToken(member.getUserId(), refreshToken);

        System.out.println("🟢 [로그인 성공] Access Token, Refresh Token을 쿠키에 저장 완료");

        return ResponseEntity.ok(Map.of("message", "로그인 성공"));
    }


    /**
     * ✅ 로그인 상태 확인 API (쿠키에서 JWT 확인)
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkLoginStatus(HttpServletRequest request, HttpServletResponse response) {
        String token = getJwtFromCookies(request);
        
        if (token != null && jwtUtil.isTokenValid(token)) {
            String role = jwtUtil.extractRole(token);
            return ResponseEntity.ok(Map.of("loggedIn", true, "role", role));
        }

        // ✅ Access Token이 없거나 만료되었을 경우, Refresh Token 확인 후 자동 갱신
        String refreshToken = jwtUtil.getRefreshTokenFromCookies(request);
        if (refreshToken != null && jwtUtil.isTokenValid(refreshToken)) {
            String userId = jwtUtil.extractUserId(refreshToken);
            String role = jwtUtil.extractRole(refreshToken);

            if (userId != null && role != null) {
                // ✅ 새로운 Access Token 발급
                String newAccessToken = jwtUtil.generateToken(userId, role);
                
                Cookie accessTokenCookie = new Cookie("jwt", newAccessToken);
                accessTokenCookie.setHttpOnly(true);
                accessTokenCookie.setSecure(true);
                accessTokenCookie.setPath("/");
                accessTokenCookie.setMaxAge(30 * 60);
                response.addCookie(accessTokenCookie);

                System.out.println("🟢 [checkLoginStatus] Access Token 재발급 완료: " + userId + ", 역할: " + role);

                return ResponseEntity.ok(Map.of("loggedIn", true, "role", role)); // ✅ 로그인 상태 유지
            }
        }

        return ResponseEntity.ok(Map.of("loggedIn", false)); // ❌ Refresh Token도 만료된 경우
    }

    /**
     * ✅ Access Token 재발급 (Refresh Token을 Redis에서 확인 후 발급)
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String userId = getUserIdFromCookies(request);

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "인증되지 않은 사용자"));
        }

        String refreshToken = redisService.getRefreshToken(userId);
        if (refreshToken == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Refresh Token이 존재하지 않음"));
        }

        // ✅ 최신 역할 가져오기 (DB에서)
        Member member = memberService.findByUserId(userId);
        if (member == null) {
            return ResponseEntity.status(401).body(Map.of("error", "사용자를 찾을 수 없음"));
        }

        String role = member.getRole();

        if (role == null || role.isEmpty()) {
            System.out.println("🔴 [오류] Refresh Token 기반 Access Token 재발급 시 role이 없음!");
            return ResponseEntity.status(401).body(Map.of("error", "사용자 역할이 없음"));
        }

        System.out.println("🟢 [Access Token 재발급] 사용자 ID: " + userId + ", 역할: " + role);

        // ✅ 새로운 Access Token 발급 (role 포함)
        String newAccessToken = jwtUtil.generateToken(userId, role);

        // ✅ Access Token을 HttpOnly 쿠키에 저장
        Cookie accessTokenCookie = new Cookie("jwt", newAccessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(30 * 60);
        response.addCookie(accessTokenCookie);

        System.out.println("🟢 [Access Token 재발급 완료] 사용자 ID: " + userId + ", 포함된 역할: " + role);

        return ResponseEntity.ok(Map.of("message", "Access Token 재발급 완료"));
    }



    /**
     * ✅ 로그아웃 (Redis에서 Refresh Token 삭제 + 쿠키 삭제)
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String userId = getUserIdFromCookies(request);
        if (userId != null) {
            redisService.deleteRefreshToken(userId);
            System.out.println("🟢 [로그아웃] Redis에서 Refresh Token 삭제 - 사용자 ID: " + userId);
        }

        // ✅ Access Token 쿠키 삭제
        Cookie accessTokenCookie = new Cookie("jwt", null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);
        response.addCookie(accessTokenCookie);

        // ✅ Refresh Token 쿠키도 삭제
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setAttribute("SameSite", "None"); 
        response.addCookie(refreshTokenCookie);

        System.out.println("🟢 [로그아웃] 성공 - Access Token 및 Refresh Token 쿠키 삭제 완료");

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
        return token != null ? jwtUtil.extractUserId(token) : null;
    }
}
