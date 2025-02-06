package com.metanet.team4.member.controller;

import com.metanet.team4.jwt.JwtUtil;
import com.metanet.team4.member.dto.PasswordChangeRequest;
import com.metanet.team4.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * ✅ 비밀번호 변경 (JSON API)
     */
    @PutMapping("/password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest request, HttpServletRequest httpRequest) {
        String userId = getUserIdFromRequest(httpRequest);
        userService.changePassword(userId, request);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    /**
     * ✅ 프로필 사진 변경 (JSON API)
     */
    @PutMapping("/profile-pic")
    public ResponseEntity<String> updateProfilePic(@RequestParam("file") MultipartFile file, HttpServletRequest httpRequest) {
        String userId = getUserIdFromRequest(httpRequest);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어 있습니다.");
        }

        try {
            userService.updateProfilePic(userId, file);
            return ResponseEntity.ok("프로필 사진이 변경되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("파일 업로드 실패: " + e.getMessage());
        }
    }

    /**
     * ✅ 사용자 스스로 회원 탈퇴 (계정 삭제)
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(HttpServletRequest httpRequest) {
        String userId = getUserIdFromRequest(httpRequest);
        userService.deleteUser(userId);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }

    /**
     * ✅ 자신의 장애인 인증서 조회 기능 추가
     */
    @GetMapping("/certificate")
    public ResponseEntity<String> getDisabilityCertificate(HttpServletRequest httpRequest) {
        String userId = getUserIdFromRequest(httpRequest);
        String certificate = userService.getDisabilityCertificate(userId);

        if (certificate == null || certificate.isEmpty()) {
            return ResponseEntity.ok("등록된 장애인 인증서가 없습니다.");
        }

        return ResponseEntity.ok(certificate);
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
}
