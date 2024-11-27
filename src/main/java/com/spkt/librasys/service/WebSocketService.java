package com.spkt.librasys.service;

import com.spkt.librasys.dto.response.LoanTransactionResponse;
import com.spkt.librasys.dto.response.notification.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Gửi thông báo đến người dùng cụ thể qua WebSocket
     *
     * @param username       username của người dùng nhận thông báo
     * @param notification Nội dung thông báo
     */
    public void sendNotificationToUser(String username, NotificationResponse notification) {
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", notification);
    }
    public void sendUpdateStatusLoan(String username, LoanTransactionResponse loanResponse){
        messagingTemplate.convertAndSendToUser(username, "/queue/loans", loanResponse);
    }
}
