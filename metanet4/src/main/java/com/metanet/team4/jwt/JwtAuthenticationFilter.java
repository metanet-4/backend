package com.metanet.team4.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

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
        System.out.println("🟢 [JWT 필터] 요청 URL: " + requestURI);

        // ✅ 인증 없이 접근 가능한 URL 예외 처리
        if (isPublicEndpoint(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 쿠키에서 Access Token 가져오기
        String token = getJwtFromCookies(request);

        if (token != null && jwtUtil.isTokenValid(token)) {
            authenticateUser(token);
        } else {
            System.out.println("🔴 [JWT 필터] Access Token 없음 또는 유효하지 않음");

            // ✅ Refresh Token 검사 후 Access Token 자동 갱신
            String refreshToken = getRefreshTokenFromCookies(request);
            if (refreshToken != null) {
                String userid = jwtUtil.extractUserid(refreshToken);
                if (jwtUtil.isRefreshTokenValid(userid, refreshToken)) {
                    String role = jwtUtil.extractRole(refreshToken);
                    String newAccessToken = jwtUtil.generateToken(userid, role);

                    // ✅ 새로운 Access Token을 응답 헤더에 추가
                    response.setHeader("Authorization", "Bearer " + newAccessToken);

                    // ✅ 새 Access Token을 쿠키에 저장
                    Cookie accessTokenCookie = new Cookie("jwt", newAccessToken);
                    accessTokenCookie.setHttpOnly(false);
                    accessTokenCookie.setSecure(true);
                    accessTokenCookie.setPath("/");
                    accessTokenCookie.setMaxAge(30 * 60);
                    response.addCookie(accessTokenCookie);

                    authenticateUser(newAccessToken);
                } else {
                    System.out.println("🔴 Refresh Token도 만료됨. 403 반환");
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                    return;
                }
            } else {
                System.out.println("🔴 Refresh Token 없음. 403 반환");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * ✅ 인증 없이 접근 가능한 URL 체크
     */
    private boolean isPublicEndpoint(String uri) {
        return uri.equals("/") || uri.startsWith("/auth/") || uri.startsWith("/static/") || uri.startsWith("/css/") || uri.startsWith("/js/");
    }

    /**
     * ✅ JWT에서 사용자 인증을 수행하여 SecurityContextHolder에 저장
     */
    private void authenticateUser(String token) {
        String userid = jwtUtil.extractUserid(token);
        String role = jwtUtil.extractRole(token);

        if (userid == null || role == null) {
            System.out.println("🔴 [JWT 필터] 토큰에서 사용자 ID 또는 역할을 추출하지 못함.");
            return;
        }

        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userid, null, Collections.singletonList(authority));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * ✅ 쿠키에서 Access Token 가져오기
     */
    private String getJwtFromCookies(HttpServletRequest request) {
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
