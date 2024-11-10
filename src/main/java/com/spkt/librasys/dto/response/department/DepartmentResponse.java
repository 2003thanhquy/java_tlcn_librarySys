package com.spkt.librasys.dto.response.department;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponse {
    Long departmentId;
    Integer departmentCodeId;
    String departmentCode;
    String departmentName;
    String description;
    // Không bao gồm programClasses để tránh vòng lặp
}
