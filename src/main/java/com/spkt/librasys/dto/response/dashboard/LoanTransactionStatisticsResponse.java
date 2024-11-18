package com.spkt.librasys.dto.response.dashboard;
import lombok.Builder;
import lombok.*;

import java.util.List;
import java.util.Map;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanTransactionStatisticsResponse {

    private List<Map<String, Object>> loansByMonth; // Lượt mượn trả theo tháng
    private long violatorsCount; // Số người vi phạm
    private List<Map<String, Object>> overdueBooksByMonth; // Số lượng sách trả quá hạn theo tháng
    private List<Map<String, Object>> damagedBooksByMonth; // Số lượng sách bị hư hỏng theo tháng
    private List<Map<String, Object>> loanActivities; // Bảng hoạt động mượn trả
}
