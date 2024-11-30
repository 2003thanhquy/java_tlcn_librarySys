package com.spkt.librasys.dto.response.readingSession;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReadingSessionResponse {

    Long sessionId; // ID của phiên đọc
    String userId; // ID người dùng
    Long documentId; // ID tài liệu
    int currentPage; // Số trang hiện tại
    LocalDateTime lastReadAt; // Thời gian đọc lần cuối

}
