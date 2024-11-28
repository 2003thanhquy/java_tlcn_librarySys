package com.spkt.librasys.dto.request.accessHistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO chứa các tham số tìm kiếm lịch sử truy cập.
 * Dùng để gửi các tham số tìm kiếm từ client lên server.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessHistorySearchDTO {

    /**
     * ID người dùng để lọc lịch sử truy cập.
     */
    private String userId;

    /**
     * ID tài liệu để lọc lịch sử truy cập.
     */
    private Long documentId;

    /**
     * Hành động người dùng thực hiện (ví dụ: "view", "edit").
     */
    private String activity;

    /**
     * Ngày bắt đầu lọc.
     */
    private String fromDate;

    /**
     * Ngày kết thúc lọc.
     */
    private String toDate;
}
