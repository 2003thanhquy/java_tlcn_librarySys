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
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

/**
 * Lớp Controller xử lý các yêu cầu liên quan đến thông báo (notification).
 * Cung cấp các endpoint để tạo, lấy, đánh dấu đã đọc và xóa các thông báo.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    NotificationService notificationService;
    PaginationConfig paginationConfig;

    /**
     * Lấy thông báo chi tiết theo ID.
     * Endpoint này cho phép người dùng truy vấn thông báo dựa trên ID.
     *
     * @param id ID của thông báo cần lấy.
     * @return ApiResponse chứa thông báo tương ứng với ID.
     */
    @GetMapping("/{id}")
    public ApiResponse<NotificationResponse> getNotification(@PathVariable Long id) {
        return ApiResponse.<NotificationResponse>builder()
                .result(notificationService.getNotificationById(id))
                .build();
    }

    /**
     * Lấy danh sách tất cả các thông báo với phân trang.
     * Endpoint này cho phép người dùng truy vấn danh sách các thông báo có phân trang.
     *
     * @param pageable Thông tin phân trang (số trang, kích thước trang).
     * @return ApiResponse chứa danh sách các thông báo theo phân trang.
     */
    @GetMapping
    public ApiResponse<PageDTO<NotificationResponse>> getAllNotifications(
            Pageable pageable
    ) {
        int size  = pageable.getPageSize();
        int maxSize = paginationConfig.getMaxSize();
        size = Math.min(size, maxSize);
        Pageable modifiedPageable = PageRequest.of(pageable.getPageNumber(), size, Sort.by(Sort.Order.desc("createdAt")));

        Page<NotificationResponse> notifications = notificationService.getAllNotifications(modifiedPageable);
        PageDTO<NotificationResponse> pageDTO = new PageDTO<>(notifications);
        return ApiResponse.<PageDTO<NotificationResponse>>builder()
                .result(pageDTO)
                .build();
    }

    /**
     * Lấy danh sách thông báo của người dùng hiện tại với phân trang.
     * Endpoint này cho phép người dùng lấy các thông báo của chính mình.
     *
     * @param pageable Thông tin phân trang (số trang, kích thước trang).
     * @return ApiResponse chứa danh sách các thông báo của người dùng hiện tại.
     */
    @GetMapping("/my-notifications")
    public ApiResponse<PageDTO<NotificationResponse>> getMyNotifications(
            Pageable pageable
    ) {
        int size  = pageable.getPageSize();
        int maxSize = paginationConfig.getMaxSize();
        size = Math.min(size, maxSize);
        Pageable modifiedPageable = PageRequest.of(pageable.getPageNumber(), size, Sort.by(Sort.Order.desc("createdAt")));

        // Lấy dữ liệu thông báo cho người dùng hiện tại
        Page<NotificationResponse> notifications = notificationService.getNotificationsForCurrentUser(modifiedPageable);
        PageDTO<NotificationResponse> pageDTO = new PageDTO<>(notifications);
        return ApiResponse.<PageDTO<NotificationResponse>>builder()
                .result(pageDTO)
                .build();
    }

    /**
     * Tạo một thông báo mới.
     * Endpoint này cho phép người dùng hoặc hệ thống gửi thông báo mới.
     *
     * @param request Thông tin cần thiết để tạo thông báo.
     * @return ApiResponse thông báo gửi thành công.
     */
    @PostMapping
    public ApiResponse<Void> createNotification(@RequestBody @Valid NotificationCreateRequest request) {
        notificationService.createNotifications(request);
        return ApiResponse.<Void>builder()
                .message("Gửi thông báo thành công")
                .build();
    }

    /**
     * Đánh dấu thông báo là đã đọc.
     * Endpoint này cho phép người dùng đánh dấu một thông báo là đã đọc.
     *
     * @param id ID của thông báo cần đánh dấu là đã đọc.
     * @return ApiResponse chứa thông báo đã được đánh dấu là đã đọc.
     */
    @PatchMapping("/{id}/mark-read")
    public ApiResponse<NotificationResponse> markAsRead(@PathVariable Long id) {
        return ApiResponse.<NotificationResponse>builder()
                .result(notificationService.markAsRead(id))
                .message("Thông báo đã được đánh dấu là đã đọc")
                .build();
    }

    /**
     * Đánh dấu tất cả các thông báo là đã đọc.
     * Endpoint này cho phép người dùng đánh dấu tất cả các thông báo chưa đọc của mình là đã đọc.
     *
     * @return ApiResponse thông báo rằng tất cả thông báo đã được đánh dấu là đã đọc.
     */
    @PatchMapping("/mark-all-read")
    public ApiResponse<Void> markAllRead() {
        notificationService.markAllRead();
        return ApiResponse.<Void>builder()
                .message("Tất cả thông báo đã được đánh dấu là đã đọc")
                .build();
    }

    /**
     * Xóa một thông báo.
     * Endpoint này cho phép người dùng xóa thông báo theo ID.
     *
     * @param id ID của thông báo cần xóa.
     * @return ApiResponse thông báo xóa thành công.
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ApiResponse.<String>builder()
                .result("Thông báo đã được xóa")
                .build();
    }

    /**
     * Lấy số lượng thông báo chưa đọc của người dùng hiện tại.
     * Endpoint này cho phép người dùng kiểm tra số lượng thông báo chưa đọc.
     *
     * @return ApiResponse chứa số lượng thông báo chưa đọc.
     */
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadNotificationCount() {
        Long unreadCount = notificationService.getUnreadNotificationCountForCurrentUser();
        return ApiResponse.<Long>builder()
                .result(unreadCount)
                .message("Đã lấy số lượng thông báo chưa đọc thành công")
                .build();
    }
}
