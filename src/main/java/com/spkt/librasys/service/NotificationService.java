package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.notification.NotificationCreateRequest;
import com.spkt.librasys.dto.response.notification.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    // Tạo thông báo mới
    void createNotifications(NotificationCreateRequest request);

    // Lấy tất cả thông báo cho người dùng hiện tại (pagination)
    Page<NotificationResponse> getNotificationsForCurrentUser(Pageable pageable);

    // Lấy tất cả thông báo (dành cho admin)
    Page<NotificationResponse> getAllNotifications(Pageable pageable);

    // Đánh dấu thông báo là đã đọc
    NotificationResponse markAsRead(Long notificationId);

    // Đánh dấu thông báo tat ca là đã đọc
    void markAllRead();

    // Xóa thông báo
    void deleteNotification(Long notificationId);

    // Lấy thông báo theo ID
    NotificationResponse getNotificationById(Long notificationId);
}
