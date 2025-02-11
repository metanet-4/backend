package com.metanet.team4.member.controller;

import com.metanet.team4.jwt.JwtUtil;
import com.metanet.team4.member.dto.MemberUpdateRequest;
import com.metanet.team4.member.model.Member;
import com.metanet.team4.member.service.RedisService;
import com.metanet.team4.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RedisService redisService; // ✅ Redis 서비스 추가

    /**
     * ✅ 회원 정보 수정
     */
    @PutMapping("/updateInfo")
    public ResponseEntity<String> updateUser(@RequestBody MemberUpdateRequest request, HttpServletRequest httpRequest) {
        String userId = getUserIdFromRequest(httpRequest);
        request.setUserId(userId); // JWT에서 추출한 userId 사용
        userService.updateUserInfo(request);
        return ResponseEntity.ok("회원 정보가 성공적으로 수정되었습니다.");
    }
    
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(HttpServletRequest request) {
        String userId = getUserIdFromRequest(request);
        
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        Member member = userService.getUserInfo(userId);

        if (member == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        Map<String, Object> response = Map.of(
            "userId", member.getUserId(),
            "name", member.getName(),
            "email", member.getEmail(),
            "gender", member.getGender(),
            "phone", member.getPhone(),
            "birthday", member.getBirthday()
        );

        return ResponseEntity.ok(response);
    }


    /**
     * ✅ 비밀번호 변경
     */
    @PutMapping("/password")
    public ResponseEntity<String> changePassword(@RequestBody MemberUpdateRequest request, HttpServletRequest httpRequest) {
        String userId = getUserIdFromRequest(httpRequest);
        userService.updateUserInfo(request);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    //프로필 사진 조회
    @GetMapping("/profile-pic")
    public ResponseEntity<byte[]> getProfilePic(HttpServletRequest httpRequest) {
        String userId = getUserIdFromRequest(httpRequest);
        byte[] profileImage = userService.getProfilePic(userId);

        if (profileImage == null || profileImage.length == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(profileImage);
    }


    /**
     * ✅ 프로필 사진 변경
     */
    @PutMapping("/profile-pic")
    public ResponseEntity<Map<String, String>> updateProfilePic(@RequestParam("file") MultipartFile file, HttpServletRequest httpRequest) {
        String userId = getUserIdFromRequest(httpRequest);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "파일이 비어 있습니다."));
        }

        try {
            userService.updateProfilePic(userId, file);
            return ResponseEntity.ok()
                    .body(Map.of("message", "프로필 사진이 변경되었습니다."));
        } catch (IOException e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "파일 업로드 실패: " + e.getMessage()));
        }
    }

    /**
     * ✅ 회원 탈퇴 (Access & Refresh Token 삭제 포함)
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(HttpServletRequest httpRequest, HttpServletResponse response) {
        String userId = getUserIdFromRequest(httpRequest);

        // ✅ 회원 정보 삭제
        userService.deleteUser(userId);

        // ✅ Redis에서 Refresh Token 삭제
        redisService.deleteRefreshToken(userId);

        // ✅ 쿠키에서 JWT 삭제 (Access & Refresh)
        deleteCookie(response, "jwt");
        deleteCookie(response, "refreshToken");

        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다. 모든 JWT가 삭제되었습니다.");
    }

    /**
     * ✅ 장애인 인증서 조회
     */
    @GetMapping("/certificate")
    public ResponseEntity<byte[]> getDisabilityCertificate(HttpServletRequest httpRequest) {
        String userId = getUserIdFromRequest(httpRequest);
        byte[] certificateBytes = userService.getDisabilityCertificate(userId);

        if (certificateBytes == null || certificateBytes.length == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(certificateBytes);
    }

    /**
     * ✅ 장애인 인증서 변경
     */
    @PutMapping("/certificate")
    public ResponseEntity<Map<String, String>> updateCertificate(@RequestParam("file") MultipartFile file, HttpServletRequest httpRequest) {
        String userId = getUserIdFromRequest(httpRequest);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "파일이 비어 있습니다."));
        }

        try {
            userService.updateDisabilityCertificate(userId, file);
            return ResponseEntity.ok()
                    .body(Map.of("message", "장애인 인증서가 변경되었습니다."));
        } catch (IOException e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "파일 업로드 실패: " + e.getMessage()));
        }
    }

    /**
     * ✅ 쿠키에서 JWT 토큰 파싱 → userId 추출
     */
    private String getUserIdFromRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    if (jwtUtil.isTokenValid(token)) {
                        return jwtUtil.extractUserId(token);
                    }
                }
            }
        }
        throw new RuntimeException("인증된 사용자가 아닙니다.");
    }

    /**
     * ✅ 응답에서 쿠키 삭제하는 메서드
     */
    private void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료
        response.addCookie(cookie);
    }
}
