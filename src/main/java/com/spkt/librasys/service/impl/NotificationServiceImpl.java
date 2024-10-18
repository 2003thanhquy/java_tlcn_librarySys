package com.spkt.librasys.service.impl;

import com.spkt.librasys.dto.request.notification.NotificationCreateRequest;
import com.spkt.librasys.dto.request.notification.NotificationAllUsersRequest;
import com.spkt.librasys.dto.response.notification.NotificationResponse;
import com.spkt.librasys.entity.Notification;
import com.spkt.librasys.entity.User;
import com.spkt.librasys.entity.enums.NotificationStatus;
import com.spkt.librasys.exception.AppException;
import com.spkt.librasys.exception.ErrorCode;
import com.spkt.librasys.mapper.NotificationMapper;
import com.spkt.librasys.repository.NotificationRepository;
import com.spkt.librasys.repository.access.UserRepository;
import com.spkt.librasys.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public NotificationResponse createNotification(NotificationCreateRequest request) {
        User user = userRepository.findByUsername(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Notification notification = Notification.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .status(NotificationStatus.UNREAD)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toNotificationResponse(savedNotification);
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public List<NotificationResponse> getAllNotificationsForUser(String userId) {
        User user = userRepository.findByUsername(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Notification> notifications = notificationRepository.findAllByUser(user);
        return notifications.stream()
                .map(notificationMapper::toNotificationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public NotificationResponse updateNotificationStatus(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        notification.setStatus(NotificationStatus.READ);
        Notification updatedNotification = notificationRepository.save(notification);
        return notificationMapper.toNotificationResponse(updatedNotification);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void notifyAllUsers(NotificationAllUsersRequest request) {
//        List<User> users = (request.getGroupName() == null) ?
//                userRepository.findAll() :
//                userRepository.findByGroupName(request.getGroupName());
//
//        for (User user : users) {
//            Notification notification = Notification.builder()
//                    .user(user)
//                    .title(request.getTitle())
//                    .content(request.getContent())
//                    .createdAt(LocalDateTime.now())
//                    .status(NotificationStatus.UNREAD)
//                    .groupName(request.getGroupName())
//                    .build();
//
//            notificationRepository.save(notification);
//        }
    }
}
