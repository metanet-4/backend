package com.metanet.team4.common;

import com.metanet.team4.member.model.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private Member member;
   
}

