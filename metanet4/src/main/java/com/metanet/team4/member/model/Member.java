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
    
    private byte[] image; // ✅ BLOB 타입은 byte[]로 변경
    private byte[] disabilityCertificate; // ✅ BLOB 타입은 byte[]로 변경

    private Integer isDiscounted = 0;  // 🎯 기본값 0 (일반 사용자), 관리자가 승인하면 1로 변경
    private String role;
}
