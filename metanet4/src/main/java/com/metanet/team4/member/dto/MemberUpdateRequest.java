package com.metanet.team4.member.dto;

import lombok.Data;

@Data
public class MemberUpdateRequest {
    private String userId;         // 수정 불가
    private String name;
    private String email;
    private String password;
    private String password2;
}
