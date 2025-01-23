package com.metanet.team4.jwt;

import java.sql.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.metanet.team4.member.model.Member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {
	
	private static SecretKey key = Jwts.SIG.HS256.key().build();
	
	private long tokenValidTime = 30 * 60 * 1000L;
	@Autowired
	UserDetailsService userDetailsService;
	
	public String generateToken(Member member) {
		long now = System.currentTimeMillis();
		Claims claims = Jwts.claims()
				.subject(member.getUserid())
				.issuer(member.getName())
				.issuedAt(new Date(now))
				.expiration(new Date(now+ tokenValidTime))
				.add("roles", member.getRole())
				.build();
		
		return Jwts.builder()
				.claims(claims)
				.signWith(key)
				.compact();
	}
	
	public String resolveToken(HttpServletRequest request) {
		return request.getHeader("X-AUTH-TOKEN");
	}
	
	public String getUserId(String token) {
		log.info(token);
		return Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token)
					.getPayload()
					.getSubject();
	}
	
	public Authentication getAuthentication(String token) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserId(token));
		log.info(userDetails.getUsername());
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}
	
	public boolean validateToken(String token) {
		try {
			Jws<Claims>  claims = Jwts.parser()
													.verifyWith(key)
													.build()
													.parseSignedClaims(token);
			return !claims.getPayload().getExpiration().before(new Date(tokenValidTime));
		}catch(Exception e) {
			return false;
		}
	}
}