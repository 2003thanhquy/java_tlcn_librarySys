package com.spkt.librasys.service;

import com.spkt.librasys.dto.request.notification.NotificationCreateRequest;
import com.spkt.librasys.dto.response.notification.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Giao diện NotificationService định nghĩa các hành vi liên quan đến thông báo.
 */
public interface NotificationService {

    /**
     * Tạo thông báo mới.
     *
     * @param request đối tượng chứa thông tin cần thiết để tạo thông báo.
     */
    void createNotifications(NotificationCreateRequest request);

    /**
     * Lấy tất cả thông báo cho người dùng hiện tại với phân trang.
     *
     * @param pageable đối tượng phân trang.
     * @return Page<NotificationResponse> chứa danh sách các thông báo.
     */
    Page<NotificationResponse> getNotificationsForCurrentUser(Pageable pageable);

    /**
     * Lấy tất cả thông báo (dành cho admin) với phân trang.
     *
     * @param pageable đối tượng phân trang.
     * @return Page<NotificationResponse> chứa danh sách các thông báo.
     */
    Page<NotificationResponse> getAllNotifications(Pageable pageable);

    /**
     * Đánh dấu thông báo là đã đọc.
     *
     * @param notificationId ID của thông báo cần đánh dấu là đã đọc.
     * @return NotificationResponse chứa thông tin thông báo đã được đánh dấu là đã đọc.
     */
    NotificationResponse markAsRead(Long notificationId);

    /**
     * Đánh dấu tất cả các thông báo là đã đọc.
     */
    void markAllRead();

    /**
     * Xóa thông báo.
     *
     * @param notificationId ID của thông báo cần xóa.
     */
    void deleteNotification(Long notificationId);

    /**
     * Lấy thông báo theo ID.
     *
     * @param notificationId ID của thông báo cần lấy.
     * @return NotificationResponse chứa thông tin của thông báo.
     */
    NotificationResponse getNotificationById(Long notificationId);

    /**
     * Đếm số lượng thông báo chưa đọc của người dùng hiện tại.
     *
     * @return số lượng thông báo chưa đọc.
     */
    Long getUnreadNotificationCountForCurrentUser();
}
