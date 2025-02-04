package com.metanet.team4.member.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class Member {
    private String userid;
    private String name;
    private String password;
    private String phone;
    private String email;
    private String role;

    // 회원가입 시 비밀번호 확인용으로만 쓰려면, DB 컬럼 연결은 안 하는 게 일반적
    // DB 저장 불필요한 필드는 DTO로만 처리하는 것이 보통 좋습니다.
    // private String password2;
}
