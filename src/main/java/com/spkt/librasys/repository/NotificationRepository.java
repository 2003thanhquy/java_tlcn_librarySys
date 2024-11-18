package com.spkt.librasys.repository;

import com.spkt.librasys.entity.Notification;
import com.spkt.librasys.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> , JpaSpecificationExecutor<Notification> {

    // Tìm tất cả thông báo của người dùng
    Page<Notification> findAllByUser(User user, Pageable pageable);
    List<Notification> findAllByUserAndStatus(User user, Notification.NotificationStatus status);

    // Đánh dấu tất cả thông báo của người dùng có trạng thái UNREAD thành READ
    @Modifying
    @Transactional
    @Query("UPDATE notification_001 n SET n.status = 'READ' WHERE n.user = :user AND n.status = 'UNREAD'")
    void markAllAsRead(User user);

    @Query("SELECT COUNT(n) FROM notification_001 n WHERE n.user = :user AND n.status = :status")
    Long countByUserAndStatus(@Param("user") User user, @Param("status") Notification.NotificationStatus status);

}
