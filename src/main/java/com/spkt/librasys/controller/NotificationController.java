package com.spkt.librasys.controller;

import com.spkt.librasys.config.PaginationConfig;
import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.notification.NotificationCreateRequest;
import com.spkt.librasys.dto.response.ApiResponse;
import com.spkt.librasys.dto.response.notification.NotificationResponse;
import com.spkt.librasys.service.NotificationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    NotificationService notificationService;
    PaginationConfig paginationConfig;

    @GetMapping("/{id}")
    public ApiResponse<NotificationResponse> getNotification(@PathVariable Long id) {
        return ApiResponse.<NotificationResponse>builder()
                .result(notificationService.getNotificationById(id))
                .build();
    }

    @GetMapping
    public ApiResponse<PageDTO<NotificationResponse>> getAllNotifications(
            Pageable pageable
    ) {
        int size  = pageable.getPageSize();
        int maxSize = paginationConfig.getMaxSize();
        size = Math.min(size, maxSize);;
        Pageable modifiedPageable = PageRequest.of(pageable.getPageNumber(), size, Sort.by(Sort.Order.desc("createdAt")));

        Page<NotificationResponse> notifications = notificationService.getAllNotifications(modifiedPageable);
        PageDTO<NotificationResponse> pageDTO = new PageDTO<>(notifications);
        return ApiResponse.<PageDTO<NotificationResponse>>builder()
                .result(pageDTO)
                .build();
    }

    @GetMapping("/my-notifications")
    public ApiResponse<PageDTO<NotificationResponse>> getMyNotifications(
            Pageable pageable
    ) {
        int size  = pageable.getPageSize();
        int maxSize = paginationConfig.getMaxSize();
        size = Math.min(size, maxSize);;
        Pageable modifiedPageable = PageRequest.of(pageable.getPageNumber(), size, Sort.by(Sort.Order.desc("createdAt")));

        // Lấy dữ liệu thông báo cho người dùng hiện tại
        Page<NotificationResponse> notifications = notificationService.getNotificationsForCurrentUser(modifiedPageable);
        PageDTO<NotificationResponse> pageDTO = new PageDTO<>(notifications);
        return ApiResponse.<PageDTO<NotificationResponse>>builder()
                .result(pageDTO)
                .build();
    }

    @PostMapping
    public ApiResponse<Void> createNotification(@RequestBody @Valid NotificationCreateRequest request) {
        notificationService.createNotifications(request);
        return ApiResponse.<Void>builder()
                .message("Send message success")
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
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadNotificationCount() {
        Long unreadCount = notificationService.getUnreadNotificationCountForCurrentUser();
        return ApiResponse.<Long>builder()
                .result(unreadCount)
                .message("Unread notification count fetched successfully")
                .build();
    }
}
