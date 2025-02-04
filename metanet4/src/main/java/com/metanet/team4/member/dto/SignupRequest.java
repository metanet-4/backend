package com.metanet.team4.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String userid;
    private String name;
    private String password;
    private String password2;
    private String phone;
    private String email;
}
