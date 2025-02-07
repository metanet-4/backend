package com.metanet.team4.member.controller;

import com.metanet.team4.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AdminPageController {

    private final JwtUtil jwtUtil;

    /**
     * ✅ 관리자 전용 사용자 관리 페이지 반환 (ROLE_ADMIN 체크)
     */
    @GetMapping("/admin/user-management")
    public String adminUsersPage(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String role = getUserRoleFromRequest(request);

        if (!"ROLE_ADMIN".equals(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "관리자만 접근 가능합니다.");
            return "redirect:/"; // 홈으로 리디렉트
        }

        return "admin-users";  // 📌 src/main/resources/templates/admin-users.html 반환
    }

    /**
     * 🔹 JWT 쿠키에서 role 추출
     */
    private String getUserRoleFromRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    if (jwtUtil.isTokenValid(token)) {
                        return jwtUtil.extractRole(token);
                    }
                }
            }
        }
        return null;
    }
}
