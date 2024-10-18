package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.notification.NotificationCreateRequest;
import com.spkt.librasys.dto.request.notification.NotificationAllUsersRequest;
import com.spkt.librasys.dto.response.notification.NotificationResponse;

import java.util.List;

public interface NotificationService {
    NotificationResponse createNotification(NotificationCreateRequest request);
    List<NotificationResponse> getAllNotificationsForUser(String userId);
    NotificationResponse updateNotificationStatus(Long notificationId);
    void notifyAllUsers(NotificationAllUsersRequest request);
}
