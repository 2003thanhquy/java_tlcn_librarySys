package com.spkt.librasys.controller;

import com.spkt.librasys.dto.request.notification.NotificationCreateRequest;
import com.spkt.librasys.dto.request.notification.NotificationAllUsersRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.notification.NotificationResponse;
import com.spkt.librasys.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    NotificationService notificationService;

    @PostMapping
    public ApiResponse<NotificationResponse> createNotification(@RequestBody NotificationCreateRequest request) {
        NotificationResponse response = notificationService.createNotification(request);
        return ApiResponse.<NotificationResponse>builder()
                .code(1000)
                .message("Notification created successfully")
                .result(response)
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<NotificationResponse>> getAllNotificationsForUser(@PathVariable String userId) {
        List<NotificationResponse> responses = notificationService.getAllNotificationsForUser(userId);
        return ApiResponse.<List<NotificationResponse>>builder()
                .code(1000)
                .message("Notifications retrieved successfully")
                .result(responses)
                .build();
    }

    @PutMapping("/{notificationId}/read")
    public ApiResponse<NotificationResponse> updateNotificationStatus(@PathVariable Long notificationId) {
        NotificationResponse response = notificationService.updateNotificationStatus(notificationId);
        return ApiResponse.<NotificationResponse>builder()
                .code(1000)
                .message("Notification status updated successfully")
                .result(response)
                .build();
    }

    @PostMapping("/all-users")
    public ApiResponse<Void> notifyAllUsers(@RequestBody NotificationAllUsersRequest request) {
        notificationService.notifyAllUsers(request);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Notifications sent to all users successfully")
                .build();
    }
}
