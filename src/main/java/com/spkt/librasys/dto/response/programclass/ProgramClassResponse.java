package com.spkt.librasys.dto.response.programclass;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ProgramClassResponse {
    Long id;
    String year;
    int semester;
    int studentBatch;
    String departmentName;
    Set<String> courseCodes;  // Có thể thêm chi tiết về Course nếu cần thiết
}
