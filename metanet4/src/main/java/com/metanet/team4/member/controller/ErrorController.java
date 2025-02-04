package com.metanet.team4.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorController {

    @GetMapping("/403")
    public String accessDenied() {
        // templates/error/403.html (Thymeleaf 기준) 반환
        // 또는 단순 JSON 반환을 원하면 @RestController + 적절한 응답 처리
        return "403";
    }
}
