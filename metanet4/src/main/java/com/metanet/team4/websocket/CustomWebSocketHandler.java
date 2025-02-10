package com.metanet.team4.websocket;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.metanet.team4.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketService webSocketService;
    private final JwtUtil jwtUtil;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            // 쿠키 헤더에서 JWT 토큰 추출
            List<String> cookieHeaders = session.getHandshakeHeaders().get("Cookie");
            String token = null;
            if (cookieHeaders != null) {
                for (String cookieHeader : cookieHeaders) {
                    String[] cookies = cookieHeader.split(";");
                    for (String cookie : cookies) {
                        cookie = cookie.trim();
                        if (cookie.startsWith("jwt=")) {
                            token = cookie.substring("jwt=".length());
                            break;
                        }
                    }
                    if (token != null) break;
                }
            }
            if (token == null) {
                log.error("JWT 토큰을 쿠키에서 찾을 수 없습니다. 연결을 종료합니다.");
                session.close();
                return;
            }
            // JWT 토큰을 파싱하여 사용자 ID 추출
            String userId = jwtUtil.extractUserId(token);
            webSocketService.addSession(userId, session);
            log.info("사용자 [{}]의 웹소켓 연결이 성공적으로 수립되었습니다.", userId);
        } catch (Exception e) {
            log.error("웹소켓 연결 수립 중 오류 발생: {}", e.getMessage());
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 연결 종료 시에도 쿠키에서 토큰을 추출하여 사용자 ID를 확인한 후 세션 제거
        try {
            List<String> cookieHeaders = session.getHandshakeHeaders().get("Cookie");
            String token = null;
            if (cookieHeaders != null) {
                for (String cookieHeader : cookieHeaders) {
                    String[] cookies = cookieHeader.split(";");
                    for (String cookie : cookies) {
                        cookie = cookie.trim();
                        if (cookie.startsWith("jwt=")) {
                            token = cookie.substring("jwt=".length());
                            break;
                        }
                    }
                    if (token != null) break;
                }
            }
            if (token != null) {
                String userId = jwtUtil.extractUserId(token);
                webSocketService.removeSession(userId);
                log.info("사용자 [{}]의 웹소켓 연결이 종료되었습니다.", userId);
            }
        } catch (Exception e) {
            log.error("연결 종료 처리 중 오류 발생: {}", e.getMessage());
        }
    }
}