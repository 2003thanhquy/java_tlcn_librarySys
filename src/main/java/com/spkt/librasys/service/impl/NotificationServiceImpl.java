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
import com.spkt.librasys.service.SecurityContextService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {

    NotificationRepository notificationRepository;
    UserRepository userRepository;
    AuthenticationService authenticationService;
    NotificationMapper notificationMapper;
    SecurityContextService  securityContextService;

    @Override
    @Transactional
    public void createNotifications(NotificationCreateRequest request) {
        List<String> userIds = request.getUserIds();
        if (userIds == null || userIds.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Danh sách User IDs không được để trống.");
        }

        List<User> users = userRepository.findAllById(userIds);
        if (users.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy người dùng để gửi thông báo.");
        }

        List<Notification> notifications = users.stream()
                .map(user -> Notification.builder()
                        .user(user)
                        .title(request.getTitle())
                        .content(request.getContent())
                        .status(Notification.NotificationStatus.UNREAD)
                        .build())
                .collect(Collectors.toList());

        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotificationsForCurrentUser(Pageable pageable) {
        User currentUser =  securityContextService.getCurrentUser();
        Page<Notification> notifications = notificationRepository.findAllByUser(currentUser, pageable);
        return notifications.map(notificationMapper::toNotificationResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Page<NotificationResponse> getAllNotifications(Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findAll(pageable);
        return notifications.map(notificationMapper::toNotificationResponse);
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        User user =  securityContextService.getCurrentUser();
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
        User user =  securityContextService.getCurrentUser();
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
