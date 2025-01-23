package com.metanet.team4.member.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class MemberUserDetails extends User{
	private String userEmail;
	public MemberUserDetails(String username, String password,
			Collection<? extends GrantedAuthority> authorities, String userEmail) {
		super(username, password, authorities);
		this.userEmail = userEmail;
	}
	
	public String getUserEmail() {
		return this.userEmail;
	}
}

