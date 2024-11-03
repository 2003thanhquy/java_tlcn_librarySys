package com.spkt.librasys.dto.request.notification;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationCreateRequest {

    @NotNull(message = "User IDs cannot be null")
    List<String> userIds;

    @NotNull(message = "Title cannot be null")
    String title;

    @NotNull(message = "Content cannot be null")
    String content;
}
