package com.spkt.librasys.dto.response.accessHistory;

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
    Long documentId;
    String userId;
}
