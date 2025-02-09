package com.metanet.team4.websocket;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
public class NotificationController {

    private final WebSocketService webSocketService;

    // 예시: GET /notify/aaa?message=Hello
    @GetMapping("/{userId}")
    public ResponseEntity<String> sendNotification(
            @PathVariable String userId,
            @RequestParam String message) {
        webSocketService.sendNotificationToUser(userId, message);
        return ResponseEntity.ok("Notification sent to " + userId);
    }
}