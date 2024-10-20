package com.spkt.librasys.dto.response.notification;

import com.spkt.librasys.entity.Notification;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    String id;         // ID của thông báo
    String username;     // username cua nguoi nhan thong bao
    String title;      // Tiêu đề thông báo
    String content;    // Nội dung thông báo
    LocalDateTime createdAt; // Thời gian tạo thông báo
    Notification.NotificationStatus status; // Trạng thái của thông báo (READ/UNREAD)
    String groupName;  // Nhóm người dùng nhận thông báo
}

