package com.spkt.librasys.dto.response.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class DocumentStatisticsResponse {
    private long totalDocuments; // Tổng số sách
    private long borrowedDocuments; // Tổng số sách đang mượn
    private long availableDocuments; // Tổng số sách còn trong kho
    private long disabledDocuments; // Tổng số sách bị hư hỏng/mất

    private List<Map<String, Object>> documentsByType; // Sách theo từng thể loại
    private List<Map<String, Object>> documentsByCourseCode; // Sách theo danh sách code môn mở lớp
}
