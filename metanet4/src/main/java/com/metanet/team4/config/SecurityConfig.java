package com.metanet.team4.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.metanet.team4.jwt.JwtAuthenticationFilter;
import com.metanet.team4.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;
import java.util.Arrays;

@Configuration
@EnableMethodSecurity  // ✅ 추가해야 @PreAuthorize가 동작함
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ CORS 설정 추가
            .csrf(csrf -> csrf.disable()) // ✅ CSRF 비활성화
            .authorizeHttpRequests(auth -> auth
                // ✅ 정적 리소스 허용 (JavaScript, CSS, 이미지)
                .requestMatchers("/js/**", "/css/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers( "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // ✅ 인증 없이 접근 가능한 API 추가
                .requestMatchers("/", "/auth/signup", "/auth/login", "/auth/logout", "/auth/check", "/auth/refresh").permitAll()
                .requestMatchers("/auth/send-code", "/auth/verify-code").permitAll() // ✅ 이메일 인증 API 허용
                .requestMatchers("/auth/check-userId", "/auth/check-phone").permitAll()
                .requestMatchers("/health", "/health/db").permitAll()
                .requestMatchers("/movie/**").permitAll()
                .requestMatchers("/ticket/**").permitAll()

                // ✅ OPTIONS 요청을 명시적으로 허용 (CORS 문제 해결)
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                // ✅ 관리자 전용 API
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

                // ✅ 사용자 & 관리자 접근 가능 API
                .requestMatchers("/user/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                // ✅ 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            // ✅ 세션 사용 X (JWT 방식 사용)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ✅ JWT 인증 필터 적용
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    /**
     * ✅ CORS 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080", "http://metanetbhn.shop:8080", "https://metanetbhn.shop:443", "https://metanetbhn.shop")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
