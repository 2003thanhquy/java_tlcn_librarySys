package com.spkt.librasys.repository;

import com.spkt.librasys.entity.Notification;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.entity.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndStatus(User user, NotificationStatus status);
    List<Notification> findAllByUser(User user);
}