package com.spkt.librasys.repository;

import com.spkt.librasys.entity.Notification;
import com.spkt.librasys.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NotificationRepository extends JpaRepository<Notification, Long> , JpaSpecificationExecutor<Notification> {

    // Tìm tất cả thông báo của người dùng
    Page<Notification> findAllByUser(User user, Pageable pageable);
}
