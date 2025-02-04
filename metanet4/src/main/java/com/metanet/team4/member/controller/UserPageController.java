package com.metanet.team4.member.controller;

import com.metanet.team4.jwt.JwtUtil;
import com.metanet.team4.member.model.Member;
import com.metanet.team4.member.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserPageController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    /**
     * ✅ 프로필 페이지 (로그인한 사용자만 접근 가능)
     */
    @GetMapping("/user/profile")
    public String getProfilePage(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        String userid = getUserIdFromRequest(request);

        if (userid == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/auth/login";
        }

        Member member = userService.getUserInfo(userid);
        model.addAttribute("member", member);

        return "profile";
    }

    /**
     * 🔹 쿠키에서 JWT 토큰 파싱 → userid 추출
     */
    private String getUserIdFromRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    if (jwtUtil.isTokenValid(token)) {
                        return jwtUtil.extractUserid(token);
                    }
                }
            }
        }
        return null;
    }
}
