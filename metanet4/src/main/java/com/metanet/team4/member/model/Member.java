package com.metanet.team4.member.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class Member {
    private Long id;
    private String userId;
    private String name;
    private String password;
    private String phone;
    private String email;
    private Date birthday;
    private Integer gender;
    private String image; // 프로필 사진 (파일 경로 저장)
    private String disabilityCertificate; // 장애인 인증서 이미지 파일 경로
    private Integer isDiscounted = 0;  // 🎯 기본값 0 (일반 사용자), 관리자가 승인하면 1로 변경
    private String role;
}
