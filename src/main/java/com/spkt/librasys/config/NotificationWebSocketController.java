package com.spkt.librasys.config;

import com.spkt.librasys.dto.response.notification.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class NotificationWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Gửi thông báo đến người dùng cụ thể qua WebSocket
     *
     * @param userId       ID của người dùng nhận thông báo
     * @param notification Nội dung thông báo
     */
    public void sendNotificationToUser(String userId, NotificationResponse notification) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", notification);
    }
}
