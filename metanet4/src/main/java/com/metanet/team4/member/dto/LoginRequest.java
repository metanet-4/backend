package com.metanet.team4.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
	
	@JsonProperty("userId")
    private String userId;
    private String password;
}
