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
public class AuthPageController {

    private final JwtUtil jwtUtil;

    /**
     * ✅ 로그인 상태 체크 후, 로그인된 사용자는 홈으로 리디렉트
     */
    @GetMapping("/auth/signup")
    public String signupPage(HttpServletRequest request) {
        if (isUserLoggedIn(request)) {
            return "redirect:/"; // 로그인된 사용자는 홈으로
        }
        return "signup";  // 회원가입 페이지 반환
    }

    @GetMapping("/auth/login")
    public String loginPage(HttpServletRequest request) {
        if (isUserLoggedIn(request)) {
            return "redirect:/"; // 로그인된 사용자는 홈으로
        }
        return "login";  // 로그인 페이지 반환
    }

    /**
     * 🔹 JWT가 유효하면 로그인 상태로 간주
     */
    private boolean isUserLoggedIn(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return jwtUtil.isTokenValid(cookie.getValue());
                }
            }
        }
        return false;
    }
}
