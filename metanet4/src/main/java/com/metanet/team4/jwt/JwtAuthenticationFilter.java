package com.metanet.team4.jwt;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.info("requestURI : " + requestURI);

        if (isPublicEndpoint(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 쿠키에서 Access Token 가져오기
        String token = getJwtFromCookies(request);
        if (token != null && jwtUtil.isTokenValid(token)) {
            authenticateUser(token);
        } else {
            log.warn("[JWT 필터] Access Token 없음 또는 유효하지 않음");

            // Refresh Token 검사 후 Access Token 자동 갱신
            String refreshToken = getRefreshTokenFromCookies(request);
            if (refreshToken != null) {
                String userId = jwtUtil.extractUserId(refreshToken);

                if (jwtUtil.isRefreshTokenValid(userId, refreshToken)) {
                    String role = jwtUtil.extractRole(refreshToken);
                    String newAccessToken = jwtUtil.generateToken(userId, role);

                    Cookie accessTokenCookie = new Cookie("jwt", newAccessToken);
                    accessTokenCookie.setHttpOnly(true);
                    accessTokenCookie.setSecure(true); 
                    accessTokenCookie.setPath("/");
                    accessTokenCookie.setMaxAge(30 * 60);
                    accessTokenCookie.setAttribute("SameSite", "None");

                    response.addCookie(accessTokenCookie);


                    log.info("[JWT 필터] 새 Access Token을 쿠키에 저장 완료");

                    authenticateUser(newAccessToken);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                    return;
                }
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 인증 없이 접근 가능한 URL 체크
     */
    private boolean isPublicEndpoint(String uri) {
        return uri.startsWith("/v3/api-docs/") || uri.startsWith("/auth/verify-code") || uri.startsWith("/auth/send-code") 
        		|| uri.startsWith("/swagger-ui/") || uri.equals("/") || uri.startsWith("/auth/") 
        		|| uri.startsWith("/static/") || uri.startsWith("/css/") || uri.startsWith("/js/") 
        		|| uri.equals("/health") || uri.equals("/health/db") || uri.startsWith("/movie")
        		|| uri.startsWith("/ticket") || uri.startsWith("/auth/signup");
    }

    /**
     * JWT에서 사용자 인증을 수행하여 SecurityContextHolder에 저장
     */
    private void authenticateUser(String token) {
        String userId = jwtUtil.extractUserId(token);
        String role = jwtUtil.extractRole(token);

        log.info("[JWT 필터] 토큰 파싱 결과 - ID: {}, ROLE: {}", userId, role);

        if (userId == null) {
            log.error("[JWT 필터] 토큰에서 사용자 ID를 추출하지 못함.");
            return;
        }
        if (role == null) {
            log.error("[JWT 필터] 토큰에서 역할을 추출하지 못함.");
            return;
        }

        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, Collections.singletonList(authority));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("[JWT 필터] SecurityContextHolder에 사용자 인증 완료: {}", userId);
    }

    /**
     * 쿠키에서 Access Token 가져오기
     */
    public String getJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "jwt".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * ✅ 쿠키에서 Refresh Token 가져오기
     */
    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
