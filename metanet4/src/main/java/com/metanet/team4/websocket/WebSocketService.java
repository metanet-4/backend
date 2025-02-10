package com.metanet.team4.websocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class WebSocketService {

    // 사용자 ID와 WebSocketSession 매핑 (동시성 이슈를 고려하여 ConcurrentHashMap 사용)
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(String userId, WebSocketSession session) {
        sessions.put(userId, session);
        log.info("세션 등록: userId={}, sessionId={}", userId, session.getId());
    }

    public void removeSession(String userId) {
        sessions.remove(userId);
        log.info("세션 제거: userId={}", userId);
    }

    public void sendNotificationToUser(String userId, String message) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                log.info("알림 전송 성공: userId={}, message={}", userId, message);
            } catch (IOException e) {
                log.error("알림 전송 실패: userId={}, error={}", userId, e.getMessage());
                removeSession(userId);
            }
        } else {
            log.warn("사용자 [{}]의 웹소켓 세션을 찾을 수 없습니다.", userId);
        }
    }
}