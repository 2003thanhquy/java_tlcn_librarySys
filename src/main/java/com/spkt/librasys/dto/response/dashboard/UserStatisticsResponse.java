package com.spkt.librasys.dto.response.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class UserStatisticsResponse {
    private Long totalUsers;                      // Tổng số người dùng
    private Map<String, Long> usersByRole;        // Thống kê theo vai trò
    private Long activeUsers;                     // Số người dùng hoạt động gần đây
}
