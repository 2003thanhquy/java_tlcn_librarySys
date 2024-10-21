package com.spkt.librasys.controller;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.notification.NotificationCreateRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.notification.NotificationResponse;
import com.spkt.librasys.service.NotificationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    NotificationService notificationService;

    @GetMapping("/{id}")
    public ApiResponse<NotificationResponse> getNotification(@PathVariable Long id) {
        return ApiResponse.<NotificationResponse>builder()
                .result(notificationService.getNotificationById(id))
                .build();
    }

    @GetMapping
    public ApiResponse<PageDTO<NotificationResponse>> getAllNotifications(Pageable pageable) {
        Page<NotificationResponse> notifications = notificationService.getAllNotifications(pageable);
        PageDTO<NotificationResponse> pageDTO = new PageDTO<>(notifications);
        return ApiResponse.<PageDTO<NotificationResponse>>builder()
                .result(pageDTO)
                .build();
    }

    @GetMapping("/my-notifications")
    public ApiResponse<PageDTO<NotificationResponse>> getMyNotifications(Pageable pageable) {
        Page<NotificationResponse> notifications = notificationService.getNotificationsForCurrentUser(pageable);
        PageDTO<NotificationResponse> pageDTO = new PageDTO<>(notifications);
        return ApiResponse.<PageDTO<NotificationResponse>>builder()
                .result(pageDTO)
                .build();
    }

    @PostMapping
    public ApiResponse<NotificationResponse> createNotification(@RequestBody @Valid NotificationCreateRequest request) {
        return ApiResponse.<NotificationResponse>builder()
                .result(notificationService.createNotification(request))
                .build();
    }

    @PatchMapping("/{id}/mark-read")
    public ApiResponse<NotificationResponse> markAsRead(@PathVariable Long id) {
        return ApiResponse.<NotificationResponse>builder()
                .result(notificationService.markAsRead(id))
                .message("Notification has been marked as read")
                .build();
    }
    @PatchMapping("/mark-all-read")
    public ApiResponse<Void> markAllRead() {
        notificationService.markAllRead();
        return ApiResponse.<Void>builder()
//                .result()
                .message("Notification has been marked all read")
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ApiResponse.<String>builder()
                .result("Notification has been deleted")
                .build();
    }
}
