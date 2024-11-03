package com.spkt.librasys.mapper;

import com.spkt.librasys.dto.request.notification.NotificationCreateRequest;
import com.spkt.librasys.dto.response.notification.NotificationResponse;
import com.spkt.librasys.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

//    @Mapping(source = "userId", target = "user.username")
//    Notification toNotification(NotificationCreateRequest request);

    @Mapping(source = "user.username", target = "username")
    NotificationResponse toNotificationResponse(Notification notification);
}
