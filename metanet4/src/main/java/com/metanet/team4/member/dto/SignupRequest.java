package com.metanet.team4.member.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;

@Getter
@Setter
public class SignupRequest {
    private String userId;   // 기존 userid → userId로 수정
    private String name;
    private String password;
    private String password2;  // 🎯 비밀번호 확인 필드 (DB 저장 X)
    private String phone;
    private String email;

    @DateTimeFormat(pattern = "yyyy-MM-dd")  // 🎯 Spring이 자동 변환
    private Date birthday;      // 🎯 생일 추가

    private Integer gender;     // 🎯 성별 추가
    private MultipartFile disabilityCertificate; // 🎯 장애인 인증서 업로드 추가
}
