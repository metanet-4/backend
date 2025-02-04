package com.metanet.team4.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String welcome() {
        // templates/index.html (Thymeleaf) 파일을 반환
        return "index";
    }
}
