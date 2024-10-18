package com.spkt.librasys.dto.request.notification;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationAllUsersRequest {

    @NotNull(message = "Title cannot be null")
    String title;

    @NotNull(message = "Content cannot be null")
    String content;

    String groupName; // Tùy chọn: Để gửi cho nhóm cụ thể (ví dụ: "ADMIN", "USER")
}
