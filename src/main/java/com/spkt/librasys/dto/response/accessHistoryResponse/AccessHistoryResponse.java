package com.spkt.librasys.dto.response.accessHistoryResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccessHistoryResponse {
    String accessId;
    LocalDateTime accessTime;
    String activity;  // Ví dụ: "READ", "DOWNLOAD"
    String documentId;
    String userId;
}
