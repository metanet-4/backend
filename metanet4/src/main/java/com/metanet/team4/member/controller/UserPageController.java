package com.metanet.team4.member.controller;

import com.metanet.team4.jwt.JwtUtil;
import com.metanet.team4.member.model.Member;
import com.metanet.team4.member.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.Base64;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserPageController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    /**
     * ✅ 프로필 페이지 (로그인한 사용자만 접근 가능)
     */
    @GetMapping("/profile")
    public String getProfilePage(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        String userId = getUserIdFromRequest(request);

        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/auth/login";
        }

        Member member = userService.getUserInfo(userId);
        model.addAttribute("member", member);

        // ✅ 장애인 인증서 BLOB 데이터를 Base64로 변환하여 이미지로 전달
        byte[] certificateBytes = userService.getDisabilityCertificate(userId);
        if (certificateBytes == null || certificateBytes.length == 0) {
            model.addAttribute("disabilityCertificate", null);
        } else {
            String encodedCertificate = "data:image/png;base64," + Base64.getEncoder().encodeToString(certificateBytes);
            model.addAttribute("disabilityCertificate", encodedCertificate);
        }

        return "profile";
    }

    /**
     * ✅ 회원 정보 수정 페이지
     */
    @GetMapping("/updateInfo")
    public String showUpdatePage(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        String userId = getUserIdFromRequest(request);

        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/auth/login";
        }

        Member member = userService.getUserInfo(userId);
        model.addAttribute("member", member);

        return "updateInfo";  // ✅ 회원 정보 수정 페이지 렌더링
    }
    
    
    @GetMapping("/delete")
    public String showDeletePage(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        String userId = getUserIdFromRequest(request);

        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/auth/login";
        }

        Member member = userService.getUserInfo(userId);
        model.addAttribute("member", member); // ✅ member 객체 전달

        return "delete";  
    }


    /**
     * ✅ 장애인 인증서 확인 페이지
     */
    public String getCertificatePage(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        String userId = getUserIdFromRequest(request);

        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/auth/login";
        }

        byte[] certificateBytes = userService.getDisabilityCertificate(userId);
        if (certificateBytes.length == 0) {
            model.addAttribute("disabilityCertificate", null);  // ✅ NULL 방지
        } else {
            String encodedCertificate = "data:image/png;base64," + Base64.getEncoder().encodeToString(certificateBytes);
            model.addAttribute("disabilityCertificate", encodedCertificate);
        }


        return "certificate";
    }

    /**
     * 🔹 쿠키에서 JWT 토큰 파싱 → userId 추출
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
        return null;
    }
}
