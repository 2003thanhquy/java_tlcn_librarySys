package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.PageDTO;
import com.spkt.librasys.dto.request.notification.NotificationCreateRequest;
import com.spkt.librasys.dto.response.notification.NotificationResponse;
import com.spkt.librasys.entity.Notification;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.NotificationMapper;
import com.spkt.librasys.repository.NotificationRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.service.AuthenticationService;
import com.spkt.librasys.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {

    NotificationRepository notificationRepository;
    UserRepository userRepository;
    AuthenticationService authenticationService;
    NotificationMapper notificationMapper;

    @Override
    @Transactional
    public NotificationResponse createNotification(NotificationCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Notification notification = Notification.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .status(Notification.NotificationStatus.UNREAD)
                .build();

        notificationRepository.save(notification);
        return notificationMapper.toNotificationResponse(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotificationsForCurrentUser(Pageable pageable) {
        User currentUser = authenticationService.getCurrentUser();
        Page<Notification> notifications = notificationRepository.findAllByUser(currentUser, pageable);
        return notifications.map(notificationMapper::toNotificationResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Page<NotificationResponse> getAllNotifications(Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findAll(pageable);
        return notifications.map(notificationMapper::toNotificationResponse);
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        User user = authenticationService.getCurrentUser();
        if(user == null)
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        if(!user.equals(notification.getUser()))
            throw new AppException(ErrorCode.UNAUTHORIZED, "User current != user Notification");

        notification.setStatus(Notification.NotificationStatus.READ);
        notificationRepository.save(notification);
        return notificationMapper.toNotificationResponse(notification);
    }

    @Override
    public void markAllRead() {
        User user = authenticationService.getCurrentUser();
        if(user == null) throw new AppException(ErrorCode.USER_NOT_FOUND);

       //Notification notification = notificationRepository.findAllByUser(user);
        notificationRepository.markAllAsRead(user);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notificationRepository.delete(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        return notificationMapper.toNotificationResponse(notification);
    }
}
